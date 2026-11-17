// ---------- state ----------
// the JWT from /auth/login; kept in localStorage so a page refresh doesn't log you out
let token = localStorage.getItem("token");
let me = null;          // {id, name} from GET /auth/me, used to spot "my" books
let editingId = null;   // id of the book being edited, or null when the form is in "sell" mode
let page = 0;           // current page of /books (Spring pages start at 0)
const PAGE_SIZE = 5;

// ---------- one helper for every API call ----------
// adds the JSON header + the token, and turns any non-2xx into an Error with a readable message
async function api(path, options = {}) {
    const headers = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = "Bearer " + token;

    const res = await fetch(path, { ...options, headers });
    const text = await res.text();               // body can be JSON, plain text, or empty
    const data = text ? tryJson(text) : null;

    if (!res.ok) {
        // 401 from Spring Security has an empty body → our own message
        if (res.status === 401) {
            if (token) logout();                 // token expired or user gone
            throw new Error("Please log in first.");
        }
        // validation errors come back as {"field": "message", ...}
        if (data && typeof data === "object") {
            throw new Error(Object.entries(data).map(([f, m]) => f + " " + m).join(", "));
        }
        throw new Error(data || "Request failed (" + res.status + ")");
    }
    return data;
}

function tryJson(text) {
    try { return JSON.parse(text); } catch { return text; }
}

// ---------- small UI helpers ----------
function showMessage(text, kind) {
    const box = document.getElementById("message");
    box.textContent = text;
    box.className = kind;                        // "error" or "ok" → CSS color
    box.hidden = false;
}

// builds an <li> safely: textContent never runs HTML, so a book titled "<script>" stays plain text
function listItem(mainText, metaText) {
    const li = document.createElement("li");
    const left = document.createElement("div");
    const title = document.createElement("div");
    title.textContent = mainText;
    const meta = document.createElement("div");
    meta.className = "meta";
    meta.textContent = metaText;
    left.append(title, meta);
    li.append(left);
    return li;
}

// ---------- books (public) ----------
async function loadBooks() {
    const title = document.getElementById("search-title").value;
    const url = "/books?title=" + encodeURIComponent(title) + "&page=" + page + "&size=" + PAGE_SIZE + "&sort=id";
    const data = await api(url);

    // Spring may put page info at the top level or inside "page", depending on settings
    const info = data.page || data;
    const list = document.getElementById("book-list");
    list.innerHTML = "";

    if (data.content.length === 0) {
        list.append(listItem("No books found.", ""));
    }
    for (const book of data.content) {
        const seller = book.seller ? book.seller.name : "unknown";
        const li = listItem(book.title + " by " + book.author,
            book.year + " · $" + book.price.toFixed(2) + " · sold by " + seller);

        // my book → Edit + Remove; anyone else's → Buy (the server still checks: this is only for display)
        const actions = document.createElement("div");
        actions.className = "actions";
        const isMine = me && book.seller && book.seller.id === me.id;
        if (isMine) {
            actions.append(
                makeButton("Edit", "secondary", () => startEdit(book)),
                makeButton("Remove", "danger", () => removeBook(book)));
        } else {
            actions.append(makeButton("Buy", "", () => buyBook(book.id)));   // errors (403 own book, 409 sold) show in the message box
        }
        li.append(actions);
        list.append(li);
    }

    const totalPages = Math.max(info.totalPages, 1);
    document.getElementById("page-info").textContent = "Page " + (page + 1) + " of " + totalPages;
    document.getElementById("prev-btn").disabled = page === 0;
    document.getElementById("next-btn").disabled = page + 1 >= totalPages;
}

function makeButton(text, className, onClick) {
    const btn = document.createElement("button");
    btn.textContent = text;
    btn.className = className;
    btn.onclick = safe(onClick);
    return btn;
}

// ---------- auth ----------
async function login(email, password) {
    const data = await api("/auth/login", { method: "POST", body: JSON.stringify({ email, password }) });
    token = data.token;
    localStorage.setItem("token", token);
    await refresh();
    showMessage("Logged in.", "ok");
}

function logout() {
    token = null;
    me = null;
    localStorage.removeItem("token");
    cancelEdit();
    updateAuthUI();
    loadBooks().catch(err => showMessage(err.message, "error"));   // redraw: Edit/Remove buttons become Buy
}

// shows/hides sections depending on whether we know who you are
function updateAuthUI() {
    const loggedIn = !!me;
    document.getElementById("auth-section").hidden = loggedIn;
    document.getElementById("member-section").hidden = !loggedIn;
    document.getElementById("logout-btn").hidden = !loggedIn;
    document.getElementById("who").textContent = loggedIn ? "Logged in as " + me.name : "Not logged in";
}

// ask the server who the token belongs to, then redraw everything
async function refresh() {
    if (token) {
        try { me = await api("/auth/me"); }
        catch { me = null; }                   // expired token → api() already logged us out
    }
    updateAuthUI();
    await loadBooks();
    if (me) await loadOrders();
}

// ---------- logged-in actions ----------
async function buyBook(bookId) {
    if (!token) { showMessage("Log in to buy a book.", "error"); return; }
    const order = await api("/orders", { method: "POST", body: JSON.stringify({ bookId }) });
    showMessage("You bought " + order.purchasedBook.title + " for $" + order.price.toFixed(2) + ".", "ok");
    await loadBooks();      // it's sold now, so it disappears from the list
    await loadOrders();
}

async function loadOrders() {
    const orders = await api("/orders");
    const list = document.getElementById("order-list");
    list.innerHTML = "";
    if (orders.length === 0) {
        list.append(listItem("No orders yet.", ""));
    }
    for (const order of orders) {
        list.append(listItem(order.purchasedBook.title,
            "$" + order.price.toFixed(2) + " · " + new Date(order.date).toLocaleString()));
    }
}

async function sellBook(book) {
    const saved = await api("/books", { method: "POST", body: JSON.stringify(book) });
    showMessage("Listed " + saved.title + " (id " + saved.id + ").", "ok");
    await loadBooks();
}

// PUT /books/{id}: the server copies these fields onto the stored book (seller stays you)
async function saveEdit(book) {
    const saved = await api("/books/" + editingId, { method: "PUT", body: JSON.stringify(book) });
    showMessage("Saved " + saved.title + ".", "ok");
    cancelEdit();
    await loadBooks();
}

async function removeBook(book) {
    if (!confirm("Remove \"" + book.title + "\"? This can't be undone.")) return;
    await api("/books/" + book.id, { method: "DELETE" });
    showMessage("Removed " + book.title + ".", "ok");
    if (editingId === book.id) cancelEdit();
    await loadBooks();
}

// the sell form doubles as the edit form: fill it in and switch the labels
function startEdit(book) {
    editingId = book.id;
    document.getElementById("sell-title").value = book.title;
    document.getElementById("sell-author").value = book.author;
    document.getElementById("sell-year").value = book.year;
    document.getElementById("sell-price").value = book.price;
    document.getElementById("sell-heading").textContent = "Edit \"" + book.title + "\"";
    document.getElementById("sell-submit").textContent = "Save changes";
    document.getElementById("cancel-edit").hidden = false;
    document.getElementById("sell-title").focus();
}

function cancelEdit() {
    editingId = null;
    document.getElementById("sell-form").reset();
    document.getElementById("sell-heading").textContent = "Sell a book";
    document.getElementById("sell-submit").textContent = "List it";
    document.getElementById("cancel-edit").hidden = true;
}

// ---------- wire up the forms ----------
// every handler: stop the browser's own form submit (page reload), call the API, show any error
function onSubmit(formId, handler) {
    document.getElementById(formId).addEventListener("submit", async (event) => {
        event.preventDefault();
        try { await handler(event.target); }
        catch (err) { showMessage(err.message, "error"); }
    });
}

onSubmit("search-form", () => { page = 0; return loadBooks(); });

onSubmit("login-form", (form) => login(
    document.getElementById("login-email").value,
    document.getElementById("login-password").value
).then(() => form.reset()));

onSubmit("register-form", async (form) => {
    const email = document.getElementById("reg-email").value;
    const password = document.getElementById("reg-password").value;
    await api("/auth/register", { method: "POST", body: JSON.stringify({
        name: document.getElementById("reg-name").value, email, password }) });
    await login(email, password);    // register, then log straight in
    form.reset();
});

// same form, two jobs: POST a new book, or PUT the one being edited
onSubmit("sell-form", (form) => {
    const book = {
        title: document.getElementById("sell-title").value,
        author: document.getElementById("sell-author").value,
        year: Number(document.getElementById("sell-year").value),
        price: Number(document.getElementById("sell-price").value),
        available: true
    };
    if (editingId) return saveEdit(book);
    return sellBook(book).then(() => form.reset());
});
document.getElementById("cancel-edit").onclick = cancelEdit;

// buttons outside forms catch their own errors
function safe(fn) { return () => fn().catch(err => showMessage(err.message, "error")); }
document.getElementById("prev-btn").onclick = safe(() => { page--; return loadBooks(); });
document.getElementById("next-btn").onclick = safe(() => { page++; return loadBooks(); });
document.getElementById("logout-btn").onclick = () => { logout(); showMessage("Logged out.", "ok"); };

// ---------- start ----------
safe(refresh)();
