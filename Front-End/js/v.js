$(document).ready(function () {

    // Signup button click event
    $("#register-form").submit(function (e) {
        e.preventDefault();
        const email = $("#email").val();
        const name = $("#name").val();
        const address = $("#address").val();
        const nic = $("#nic").val();
        const contact = $("#contact").val();
        const password = $("#password").val();
        const confirmPassword = $("#confirmPassword").val();

        if (password !== confirmPassword) {
            errorAlert("Passwords do not match!")
            return;
        }

        Swal.fire({
            title: "Complete Registration?",
            text: "You will be redirected to login page.",
            icon: "question",
            showCancelButton: true,
            confirmButtonColor: "#13a810",
            cancelButtonColor: "#df8282",
            confirmButtonText: "Register"
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    type: "POST",
                    url: "http://localhost:8080/api/v1/user/register",
                    contentType: 'application/json',
                    data: JSON.stringify({
                        "email": email,
                        "password": password,
                        "name": name,
                        "contact": contact,
                        "address": address,
                        "nic": nic,
                        "role": null
                    }),
                    success: function (response) {
                        localStorage.setItem("token", response.data.token);
                        Swal.fire({
                            icon: "success",
                            title: "Success",
                            text: "Registration Successful!",
                        }).then(() => {
                            window.location.href = "index.html";
                        });
                    },
                    error: function (xhr, error) {
                        if (xhr){
                            checkXHR(xhr)
                            return;
                        }
                        let data = error.responseJSON.data;
                        if (data.name != null){
                            errorAlert(data.name)
                        }else if (data.nic != null){
                            errorAlert(data.nic)
                        }else if(data.address != null){
                            errorAlert(data.address)
                        }else if (data.contact != null){
                            errorAlert(data.contact)
                        }else if (data.password != null){
                            errorAlert(data.password)
                        }
                    },
                });
            }
        });


    });

    function errorAlert(message){
        showAlert("error","Oops...",message)
    }
})