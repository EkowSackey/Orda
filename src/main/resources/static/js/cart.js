const CART_KEY = 'orda_cart';

function getCart() {
    const cart = localStorage.getItem(CART_KEY);
    return cart ? JSON.parse(cart) : { vendorId: null, items: [], totalAmount: 0 };
}

function saveCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
    updateCartCount();
}

function addToCart(vendorId, itemId, itemName, price) {
    let cart = getCart();

    // Check if adding from a different vendor
    if (cart.vendorId && cart.vendorId !== vendorId) {
        if (!confirm("You can only order from one vendor at a time. Clear current cart?")) {
            return;
        }
        cart = { vendorId: vendorId, items: [], totalAmount: 0 };
    } else if (!cart.vendorId) {
        cart.vendorId = vendorId;
    }

    cart.items.push({
        menuItemId: itemId,
        itemName: itemName,
        price: price,
        selectedCustomizations: [] // Simplified for now
    });

    cart.totalAmount += price;
    saveCart(cart);
    alert('Added ' + itemName + ' to cart!');
}

function removeFromCart(index) {
    let cart = getCart();
    if (index > -1 && index < cart.items.length) {
        cart.totalAmount -= cart.items[index].price;
        cart.items.splice(index, 1);

        if (cart.items.length === 0) {
            cart.vendorId = null;
            cart.totalAmount = 0;
        }

        saveCart(cart);
        location.reload(); // Refresh to show updated cart
    }
}

function clearCart() {
    localStorage.removeItem(CART_KEY);
    location.reload();
}

function updateCartCount() {
    const cart = getCart();
    const countElement = document.getElementById('cart-count');
    if (countElement) {
        countElement.innerText = cart.items.length;
    }
}

// Initialize cart count on page load
document.addEventListener('DOMContentLoaded', updateCartCount);
