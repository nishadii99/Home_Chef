$(document).ready(function () {
    const token = localStorage.getItem('token');
    let allItems = [];      // All items loaded from backend
    let frontendCart = [];  // Cart items in memory
    let categories = [];    // Store categories for later use

    if (!token) {
        window.location.href = "register.html";
        return;
    }

    // Load items, cart, and categories on page load
    loadItems();
    loadCartFromServer();
    loadCategories(); // Load categories for dropdown

    // Event delegation for dynamically created buttons
    $(document).on('click', '.see-details-btn', showItemDetails);
    $(document).on('click', '.add-to-cart', addToCart);
    $(document).on('click', '.cart-item-remove', function () {
        const index = $(this).data('index');
        removeFromCart(index);
    });

    // Add Post Modal event listener
    $(document).on('click', '#addPostBtn, .add-post-btn', openAddPostModal);

    // ===================== Load Categories =====================
    function loadCategories() {
        categories = [
            { id: "Meals", name: "Meals", icon: "fas fa-utensils" },
            { id: "Snacks", name: "Snacks", icon: "fas fa-filter" },
            { id: "Beverages", name: "Beverages", icon: "fas fa-coffee" },
            { id: "Desserts", name: "Desserts", icon: "fas fa-ice-cream" }
        ];

        populateCategoryDropdown();
    }

    // ===================== Populate Category Dropdown =====================
    function populateCategoryDropdown() {
        const categoryDropdown = $('#itemCategory');

        // Clear existing options except the first one
        categoryDropdown.find('option').not(':first').remove();

        // Add categories to dropdown
        categories.forEach(category => {
            categoryDropdown.append(
                $('<option></option>')
                    .attr('value', category.id)
                    .text(category.name)
                    .attr('data-icon', category.icon)
            );
        });

        // Also populate the edit modal category dropdown
        const editCategoryDropdown = $('#editItemCategory');
        editCategoryDropdown.empty();
        categories.forEach(category => {
            editCategoryDropdown.append(
                $('<option></option>')
                    .attr('value', category.id)
                    .text(category.name)
            );
        });
    }

    // ===================== Load Items =====================
    function loadItems() {
        $.ajax({
            url: "http://localhost:8080/api/v1/addsItem/getAll",
            type: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function (response) {
                if (response && response.data) {
                    allItems = response.data;
                    populateCards(response.data);
                } else {
                    $('#itemsContainer').html('<p class="text-center text-danger">No items found.</p>');
                }
            },
            error: function (xhr, status, error) {
                $('#itemsContainer').html('<p class="text-center text-danger">Error loading items: ' + error + '</p>');
            }
        });
    }

    // ===================== Populate Item Cards =====================
    function populateCards(items) {
        const container = $('#itemsContainer');
        container.empty();

        items.forEach(item => {
            const card = `
                <div class="col-md-6 col-lg-3 mb-4">
                    <div class="property-item" style="padding: 15px; margin-bottom: 30px; box-shadow: 1px 1px 4px rgba(0,0,0,0.6); align-items:center;">
                        <a href="product-detail.html?id=${item.itemCode}" class="img">
                            <img src="${item.sourceImage || 'images/default.png'}"
                                 alt="${item.name || 'Product'}"
                                 class="img-fluid"
                                 onerror="this.src='images/default.png'"
                                 style="padding:10px;width:18rem;height:16rem;object-fit:cover;">
                        </a>
                        <div class="property-content">
                            <div class="price mb-2"><span>Rs.${item.price ? item.price.toFixed(2) : '0.00'}</span></div>
                            <div>
                                <span class="d-block mb-2 text-black-50">${item.location || 'N/A'}</span>
                                <span class="city d-block mb-3">${item.name || 'No name'}</span>
                                <div class="specs d-flex mb-3">
                                    <span class="d-block d-flex align-items-center me-3">
                                        <i class="fas fa-tag me-2"></i>
                                        <span class="caption">${getCategoryName(item.categoryId)}</span>
                                    </span>
                                    <span class="d-block d-flex align-items-center">
                                        <i class="fas fa-cubes me-2"></i>
                                        <span class="caption">${item.quantity || 0}</span>
                                    </span>
                                </div>
                                <div class="mb-3">
                                    <label for="quantity-${item.itemCode}" class="form-label">Quantity:</label>
                                    <input type="number"
                                           id="quantity-${item.itemCode}"
                                           class="form-control"
                                           value="1"
                                           min="1"
                                           max="${item.quantity}"
                                           style="max-width:100px;">
                                    <small class="text-muted">In stock: ${item.quantity}</small>
                                </div>
                                <div style="display:flex;flex-direction:column;">
                                    <button class="btn text-light see-details-btn btn-success py-2 px-3 mb-2"
                                            data-item-code="${item.itemCode}"
                                            style="border-radius:20px; background-color:#005555;;">
                                        See Details
                                    </button>
                                    <button class="btn text-light add-to-cart btn-warning py-2 px-3 mb-2"
                                            data-item-code="${item.itemCode}"
                                            data-name="${item.name}"
                                            data-price="${item.price}"
                                            data-image="${item.sourceImage || 'images/default.png'}"
                                            style="border-radius:20px; background-color:#198754;">
                                        Add to Cart
                                    </button>
                                    <button class="btn text-light delete-item-btn btn-danger py-2 px-3"
                                            onclick="deleteItem('${item.itemCode}')"
                                            style="border-radius:20px; background-color:#C0392B;">
                                        Delete
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>`;
            container.append(card);
        });
    }

    // ===================== Show Item Details =====================
    function showItemDetails() {
        const itemCode = $(this).data('itemCode');
        const cachedItem = allItems.find(item => item.itemCode == itemCode);
        if (cachedItem) return populateItemModal(cachedItem);

        $.ajax({
            url: `http://localhost:8080/api/v1/addsItem/${itemCode}`,
            type: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function (response) {
                if (response && response.data) populateItemModal(response.data);
                else alert('Item details not found');
            },
            error: function (xhr, status, error) {
                alert('Error loading item details: ' + error);
            }
        });
    }

    function populateItemModal(item) {
        $('#detailImage').attr('src', item.sourceImage || 'images/default.png');
        $('#detailTitle').text(item.name || 'No name');
        $('#detailId').text(item.itemCode || 'N/A');
        $('#detailCategory').text(getCategoryName(item.categoryId) || 'N/A');
        $('#detailDescription').text(item.description || 'No description available');
        $('#detailLocation').text(item.location || 'N/A');
        $('#detailQuantity').text(item.quantity || 0);
        $('#detailPrice').text(item.price ? item.price.toFixed(2) : '0.00');
        $('#itemDetailsModal').modal('show');
    }

    // ===================== Add to Cart =====================
    function addToCart() {
        const userEmail = getUserEmailFromToken();
        if (!userEmail) return Swal.fire('Error', 'User not identified', 'error');

        const itemCode = $(this).data("item-code");
        const quantity = parseInt($(`#quantity-${itemCode}`).val()) || 1;
        const image = $(this).data('image') || 'images/default.png';
        const name = $(this).data('name') || 'Unknown Item';
        const itemPrice = parseFloat($(this).data('price')) || 0;
        const totalPrice = itemPrice * quantity;

        const cartData = { userEmail, itemCode, quantity, image, name, price: totalPrice };

        $.ajax({
            url: "http://localhost:8080/api/v1/cart/save",
            type: "POST",
            contentType: "application/json",
            headers: { "Authorization": `Bearer ${token}` },
            data: JSON.stringify(cartData),
            success: function (response) {
                Swal.fire({ icon: 'success', title: 'Item added to cart!', showConfirmButton: false, timer: 1500 });
                frontendCart.push(cartData);
                renderCartDropdown();
            },
            error: function (xhr) {
                const errorMsg = xhr.responseJSON?.message || 'Failed to add to cart';
                Swal.fire('Error', errorMsg, 'error');
            }
        });
    }

    // ===================== Render Cart Dropdown =====================
    function renderCartDropdown() {
        const cartContainer = $(".dropdown-cart");
        const cartBadge = $(".cart-count-badge");

        cartBadge.text(frontendCart.length);
        cartContainer.empty();
        cartContainer.append(`<h6 class="dropdown-header">Your Cart (${frontendCart.length} items)</h6><div class="dropdown-divider"></div>`);

        let subtotal = 0;

        frontendCart.forEach((item, index) => {
            subtotal += item.price;
            const itemHtml = `
                <div class="cart-preview-item d-flex align-items-center mb-2">
                    <img src="${item.image}" alt="${item.name}" class="cart-item-image me-2">
                    <div class="cart-item-details flex-grow-1">
                        <p class="mb-1">${item.name}</p>
                        <small>${item.quantity} x Rs.${(item.price / item.quantity).toFixed(2)}</small>
                    </div>
                    <button class="btn btn-sm btn-outline-danger cart-item-remove" data-index="${index}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>`;
            cartContainer.append(itemHtml);
        });

        cartContainer.append(`<div class="dropdown-divider"></div>
            <div class="d-flex justify-content-between mb-2"><strong>Subtotal:</strong><span>Rs.${subtotal.toFixed(2)}</span></div>
            <a href="shoppingCart.html" class="btn btn-primary btn-block">View Cart</a>
            <a href="payment.html" class="btn btn-success btn-block mt-2">Checkout</a>`);
    }

    // ===================== Remove from Cart =====================
    function removeFromCart(index) {
        frontendCart.splice(index, 1);
        renderCartDropdown();
    }

    // ===================== Load Cart from Server on Page Load =====================
    function loadCartFromServer() {
        const userEmail = getUserEmailFromToken();
        if (!userEmail) return;

        $.ajax({
            url: `http://localhost:8080/api/v1/cart/get`,
            type: "GET",
            headers: { "Authorization": `Bearer ${token}` },
            success: function (response) {
                if (response && response.data) {
                    frontendCart = response.data.map(item => ({
                        userEmail: item.userEmail,
                        itemCode: item.itemCode,
                        quantity: item.quantity,
                        image: item.image || 'images/default.png',
                        name: item.name || 'Unknown',
                        price: item.price || 0
                    }));
                    renderCartDropdown();
                }
            },
            error: function () { console.log("Failed to load cart from server."); }
        });
    }

    // ===================== Utility Functions =====================
    function getUserEmailFromToken() {
        try {
            const token = localStorage.getItem("token");
            const decodedToken = jwt_decode(token);
            return decodedToken.email || decodedToken.sub || null;
        } catch (e) {
            console.error("Error decoding token:", e);
            return null;
        }
    }

    function getCategoryName(categoryId) {
        if (categories.length > 0) {
            const category = categories.find(cat => cat.id == categoryId);
            if (category) return category.name;
        }

        const fallbackCategories = {
            "Meals": "Meals",
            "Snacks": "Snacks",
            "Beverages": "Beverages",
            "Desserts": "Desserts"
        };
        return fallbackCategories[categoryId] || "Other";
    }

    function deleteItem(itemCode) {
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `http://localhost:8080/api/v1/addsItem/delete/${itemCode}`,
                    type: "DELETE",
                    headers: { "Authorization": `Bearer ${token}` },
                    success: function () {
                        Swal.fire('Deleted!', 'Your item has been deleted.', 'success');
                        loadItems();
                    },
                    error: function (xhr) {
                        const errorMsg = xhr.responseJSON?.message || 'Failed to delete item';
                        Swal.fire('Error', errorMsg, 'error');
                    }
                });
            }
        });
    }

    // expose deleteItem and loadItems globally
    window.deleteItem = deleteItem;
    window.loadItems = loadItems;
});

// ===================== Global Functions =====================
function openAddPostModal() {
    console.log("openAddPostModal called");
    clearAddPostForm();
    $('#addPostModal').modal('show');
    setTimeout(() => { $('#itemTitle').focus(); }, 500);
}

function clearAddPostForm() {
    console.log("Clearing add post form");
    $('#postAddForm')[0].reset();
    const token = localStorage.getItem("token");
    let userEmail = "";
    if (token) {
        try {
            const decodedToken = jwt_decode(token);
            userEmail = decodedToken.email || decodedToken.sub || "";
        } catch (e) { console.error("Error decoding token:", e); }
    }
    $('#itemUserEmail').val(userEmail);
    const imagePreview = $('#imagePreview');
    if (imagePreview.length) { imagePreview.attr('src', 'images/default.png'); }
    $('#itemCategory').val('none');
    $('#itemTitle').val('');
    $('#itemPrice').val('');
    $('#itemDescription').val('');
}
