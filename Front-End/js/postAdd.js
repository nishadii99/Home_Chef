$(document).ready(function () {
    // Get token from localStorage
    const token = localStorage.getItem('token');

    // Check token existence
    if (!token || token === "undefined" || token === "null") {
        redirectToLogin();
        return;
    }

    loadCategories();

    try {
        // Decode token to get user info
        console.log("Raw token:", token); // Debug
        const decoded = jwt_decode(token);
        console.log("Decoded token:", decoded); // Debug

        // Use `sub` claim as default (Spring Security convention)
        const userEmail = decoded.sub;
        if (!userEmail) {
            throw new Error("No user identifier found in token");
        }

        console.log("User email/username:", userEmail); // Debug
        $('#itemUserEmail').val(userEmail); // Updated to match HTML

        // Load categories again when modal opens
        $('#addPostModal').on('show.bs.modal', function () {
            console.log("Modal opened - loading categories");

            //set email from token
            $('#itemUserEmail').val(userEmail); // Updated to match HTML

            // Only load categories if dropdown is empty or has error message
            const currentOptions = $('#itemCategory option').length;
            const hasErrorOption = $('#itemCategory option:contains("Error")').length > 0;

            if (currentOptions <= 1 || hasErrorOption) {
                loadCategories();
            }
        });

    } catch (error) {
        console.error("Authentication error:", error);
        showError("Session invalid. Please login again.");
        redirectToLogin();
    }
    // Set email whenever Add Post button is clicked
    $('#openAddPostModalBtn').click(function () {
        try {
            const token = localStorage.getItem('token');
            if (!token) return;

            const decoded = jwt_decode(token);
            const userEmail = decoded.sub || '';
            $('#email').val(userEmail);

            console.log("Email set on Add Post click:", userEmail); // Debug
        } catch (error) {
            console.error("Failed to set email on Add Post click:", error);
        }
    });

    // Function to load categories
    function loadCategories() {
        $.ajax({
            url: "http://localhost:8080/api/v1/categories/get",
            type: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            dataType: "json",
            success: function (response) {
                console.log("Categories response:", response.data); // Debug log

                if (response.data && Array.isArray(response.data)) {
                    const $select = $('#itemCategory');
                    $select.empty().append('<option value="" disabled selected>Choose a category</option>');

                    response.data.forEach(category => {
                        $select.append(`<option value="${category.categoryId}">${category.name}</option>`);
                        console.log("Appending category:", category.categoryId);
                    });
                } else {
                    console.error("Unexpected response format:", response.data);
                    // Keep existing hardcoded options if backend fails
                    console.log("Keeping hardcoded categories as fallback");
                }
            },
            error: function (xhr, status, error) {
                console.error("Failed to load categories:", xhr.status, error);
                // Keep existing hardcoded options if backend fails
                console.log("Keeping hardcoded categories due to backend error");
                if (xhr.status === 401) {
                    showError("Session expired. Please login again.");
                    redirectToLogin();
                    return;
                }
            }
        });
    }

    // Image preview functionality
    $('#itemImages').on('change', function () {
        $('#imagePreview').empty();
        const files = this.files;

        if (files.length > 5) {
            showWarning('You can upload maximum 5 images');
            $(this).val('');
            return;
        }

        Array.from(files).forEach(file => {
            if (!file.type.match('image.*')) {
                showError('Only image files are allowed');
                $(this).val('');
                return;
            }

            const reader = new FileReader();
            reader.onload = function (e) {
                $('#imagePreview').append(`
                    <div class="image-preview-item m-2 position-relative" data-filename="${file.name}">
                        <img src="${e.target.result}" class="img-thumbnail rounded shadow-sm"
                             style="width: 100px; height: 100px; object-fit: cover;">
                        <span class="remove-image position-absolute top-0 end-0 bg-white rounded-circle p-1"
                              style="transform: translate(30%, -30%); cursor: pointer;">
                            <i class="fas fa-times text-danger"></i>
                        </span>
                    </div>
                `);
            };
            reader.readAsDataURL(file);
        });
    });

    // Remove image preview
    $(document).on('click', '.remove-image', function () {
        const filename = $(this).closest('.image-preview-item').data('filename');
        const input = $('#itemImages')[0];
        const files = Array.from(input.files);

        // Remove file
        const updatedFiles = files.filter(file => file.name !== filename);

        const dataTransfer = new DataTransfer();
        updatedFiles.forEach(file => dataTransfer.items.add(file));
        input.files = dataTransfer.files;

        $(this).closest('.image-preview-item').remove();
    });

    // Form submission
    $('#submitPost').click(function (e) {
        e.preventDefault();

        // Validate category selection
        const categoryValue = document.getElementById("itemCategorys").value;
        console.log("Form validated successfully, category:", categoryValue); // Debug
        const formData = prepareFormData();
        submitAdvertisement(formData);
    });
    // Prepare FormData
    function prepareFormData() {
        const formData = new FormData();
        formData.append('itemName', $('#itemTitle').val());
        formData.append('categoryId', $('#itemCategorys').val());
        formData.append('price', $('#itemPrice').val());
        formData.append('quantity', $('#itemQuantity').val());
        formData.append('location', $('#itemLocation').val());
        formData.append('description', $('#itemDescription').val());
        formData.append('userEmail', $('#itemUserEmail').val()); // Updated to match HTML

        Array.from($('#itemImages')[0].files).forEach(file => {
            formData.append('sourceImage', file);
        });

        return formData;
    }

    // Submit advertisement
    function submitAdvertisement(formData) {
        const submitBtn = $('#submitPost');
        submitBtn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin mr-2"></i> Posting...');
        $.ajax({
            url: 'http://localhost:8080/api/v1/addsItem/saveItem',
            type: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                if (response.code === 201) {
                    showSuccess('Your item has been posted successfully');
                    resetForm();
                    loadItems(); // Refresh items on the main page
                    $('#addPostModal').modal('hide');
                } else {
                    showError(response.message || 'Failed to post item');
                }
            },
            error: function (xhr) {
                handleSubmissionError(xhr);
            },
            complete: function () {
                submitBtn.prop('disabled', false).html('<i class="fas fa-paper-plane mr-2"></i> Post Item');
            }
        });
    }

    // Helper functions
    function scrollToFirstInvalidField() {
        const firstInvalid = $('.is-invalid').first();
        if (firstInvalid.length) {
            $('html, body').animate({
                scrollTop: firstInvalid.offset().top - 100
            }, 500);
            showError('Please fill all required fields correctly.');
        }
    }

    function resetForm() {
        $('#postAddForm')[0].reset();
        $('#postAddForm')[0].classList.remove('was-validated');
        $('#imagePreview').empty();
    }

    function redirectToLogin() {
        Swal.fire({
            icon: 'error',
            title: 'Authentication Required',
            text: 'Please login first',
            confirmButtonColor: '#28a745'
        }).then(() => {
            window.location.href = "login.html"; // ✅ fixed
        });
    }

    function showError(message) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: message,
            confirmButtonColor: '#28a745'
        });
    }

    function showWarning(message) {
        Swal.fire({
            icon: 'warning',
            title: 'Warning',
            text: message,
            confirmButtonColor: '#28a745'
        });
    }

    function showSuccess(message) {
        Swal.fire({
            icon: 'success',
            title: 'Success!',
            text: message,
            confirmButtonColor: '#28a745'
        });
    }

    function handleSubmissionError(xhr) {
        let errorMessage = 'Failed to post item. Please try again.';
        if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
        } else if (xhr.status === 413) {
            errorMessage = 'File size too large. Please upload smaller images.';
        } else if (xhr.status === 401) {
            errorMessage = 'Session expired. Please login again.';
            redirectToLogin();
            return;
        }
        showError(errorMessage);
    }

    // Clear validation styling on input
    $('input, textarea, select').on('input change', function () {
        $(this).removeClass('is-invalid');
    });
});
