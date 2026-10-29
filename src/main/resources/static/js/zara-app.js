// ZaraStyle Interactive Client

let currentUser = null;

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadGarments();
});

async function checkAuth() {
    try {
        const res = await fetch('/api/auth/me');
        if (res.ok) {
            currentUser = await res.json();
            renderUserNav();
        } else {
            currentUser = null;
            renderUserNav();
        }
    } catch (e) {
        currentUser = null;
        renderUserNav();
    }
}

function renderUserNav() {
    const el = document.getElementById('user-nav-box');
    if (currentUser && currentUser.authenticated) {
        el.innerHTML = `
            <span style="font-size: 0.75rem; text-transform: uppercase; font-weight: 700;">
                👤 ${currentUser.fullName} (${currentUser.role.replace('ROLE_', '')})
            </span>
            <button onclick="logoutStaff()" class="btn-zara" style="padding: 0.4rem 0.8rem; font-size: 0.7rem;">LOGOUT</button>
        `;
    } else {
        el.innerHTML = `
            <button onclick="openModal('login-modal')" class="btn-zara" style="padding: 0.4rem 0.8rem; font-size: 0.7rem;">STAFF LOGIN</button>
        `;
    }
}

async function logoutStaff() {
    await fetch('/api/auth/logout', { method: 'POST' });
    currentUser = null;
    checkAuth();
}

async function loadGarments() {
    const grid = document.getElementById('garments-grid');
    grid.innerHTML = '<div style="text-align: center; grid-column: 1/-1; padding: 3rem;">LOADING INDITEX CATALOG...</div>';

    const res = await fetch('/api/garments');
    const garments = await res.json();

    grid.innerHTML = garments.map(g => {
        const isDiscounted = g.discountedPrice < g.basePrice;
        return `
            <div class="garment-card">
                <div class="garment-img-box">
                    <img src="${g.imageUrl}" alt="${g.name}">
                    <span class="season-tag">${g.season}</span>
                </div>
                <div class="garment-details">
                    <div class="garment-ref">${g.refCode}</div>
                    <h3 class="garment-name">${g.name}</h3>
                    <div class="garment-comp">${g.composition}</div>

                    <div class="price-row">
                        <span class="current-price" style="${isDiscounted ? 'color: var(--accent-red);' : ''}">
                            €${g.discountedPrice.toFixed(2)}
                        </span>
                        ${isDiscounted ? `<span class="original-price">€${g.basePrice.toFixed(2)}</span>` : ''}
                    </div>

                    <div style="font-size: 0.7rem; text-transform: uppercase; font-weight: 700; margin-bottom: 0.4rem; color: var(--gray-600);">
                        Size Matrix (Floor / Backroom):
                    </div>
                    <div class="size-matrix">
                        ${g.stockList.map(s => `
                            <div class="size-pill ${s.totalStock === 0 ? 'out-of-stock' : ''}" 
                                 onclick="inspectStock(${s.id}, '${g.refCode}', '${g.name}', '${s.size}', ${s.floorStock}, ${s.backroomStock}, '${s.rfidBarcode}')">
                                <div>${s.size}</div>
                                <div class="size-stock-count">${s.floorStock}F / ${s.backroomStock}B</div>
                            </div>
                        `).join('')}
                    </div>

                    <div style="display: flex; gap: 0.5rem; margin-top: auto; padding-top: 1rem;">
                        <button class="btn-zara" style="flex: 1;" onclick="inspectStock(${g.stockList[0]?.id}, '${g.refCode}', '${g.name}', 'M', ${g.stockList[2]?.floorStock || 0}, ${g.stockList[2]?.backroomStock || 0}, '${g.stockList[2]?.rfidBarcode || ''}')">
                            INSPECT STOCK
                        </button>
                        <button class="btn-zara" style="background: var(--white); color: var(--black);" onclick="openMarkdownModal(${g.id}, '${g.name}', ${g.basePrice})">
                            MARKDOWN
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// RFID Scanner
async function performScan() {
    const input = document.getElementById('rfid-input');
    const barcode = input.value.trim();
    if (!barcode) return;

    try {
        const res = await fetch(`/api/garments/scan?barcode=${encodeURIComponent(barcode)}`);
        if (res.ok) {
            const data = await res.json();
            inspectStock(data.stockId, data.garmentRef, data.garmentName, data.size, data.floorStock, data.backroomStock, data.rfidBarcode);
        } else {
            alert('No garment found matching RFID barcode: ' + barcode);
        }
    } catch (e) {
        alert('Scan lookup failed');
    }
}

// Inspect & Replenish Stock Modal
let currentStockId = null;

function inspectStock(id, ref, name, size, floor, backroom, rfid) {
    currentStockId = id;
    document.getElementById('stock-ref').textContent = ref;
    document.getElementById('stock-name').textContent = name;
    document.getElementById('stock-size').textContent = size;
    document.getElementById('stock-floor-qty').textContent = floor;
    document.getElementById('stock-back-qty').textContent = backroom;
    document.getElementById('stock-rfid').textContent = rfid;

    openModal('stock-modal');
}

async function replenishFloor() {
    const qty = parseInt(document.getElementById('replenish-qty').value);
    if (!qty || qty <= 0) {
        alert('Please enter a valid quantity');
        return;
    }

    try {
        const res = await fetch('/api/garments/replenish', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ stockId: currentStockId, quantity: qty })
        });

        const data = await res.json();
        if (res.ok) {
            alert('Sales floor replenished with ' + qty + ' units!');
            closeModal('stock-modal');
            loadGarments();
        } else {
            alert(data.error || 'Replenishment failed');
        }
    } catch (e) {
        alert('Error communicating with replenishment service');
    }
}

// Markdown Modal
let currentMarkdownGarmentId = null;
function openMarkdownModal(id, name, basePrice) {
    currentMarkdownGarmentId = id;
    document.getElementById('md-garment-name').textContent = name;
    document.getElementById('md-base-price').textContent = `€${basePrice.toFixed(2)}`;
    openModal('markdown-modal');
}

async function submitMarkdown(e) {
    e.preventDefault();
    const pct = parseFloat(document.getElementById('markdown-percentage').value);

    const res = await fetch(`/api/garments/${currentMarkdownGarmentId}/markdown`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ percentage: pct })
    });

    if (res.ok) {
        closeModal('markdown-modal');
        loadGarments();
    } else {
        alert('Markdown failed');
    }
}

// Staff Login
async function submitLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });

    if (res.ok) {
        closeModal('login-modal');
        checkAuth();
    } else {
        alert('Invalid staff credentials');
    }
}

function openModal(id) { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }
