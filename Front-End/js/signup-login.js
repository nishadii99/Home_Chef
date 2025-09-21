$(document).ready(function() {
    $('#registerBtn').click(function (){
        let name = $('#name').val();
        let email = $('#email').val();
        let password = $('#password').val();
        let confirmPassword = $('#confirm-password').val();
        let selectedValue = $('#role').val();
        if (password !== confirmPassword) {
            alert('Password and Confirm Password do not match');
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
        }).then((result)=>{
            if(result.isConfirmed){
                $.ajax({
                    url: 'http://localhost:8080/api/v1/user/register',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        "username": name,
                        "email": email,
                        "password": password,
                        "role": selectedValue
                    }),
                    success: function(response) {
                        localStorage.setItem("token", response.data.token);
                        Swal.fire({
                            icon: "success",
                            title: "Success",
                            text: "Registration Successful!",
                        }).then(() => {
                            window.location.href = "login.html";
                        });
                    },
                    error: function(xhr,error) {
                        if (xhr){
                            checkXHR(xhr)
                            return;
                        }
                        let data = error.responseJSON.data;
                        if (data.name != null){
                            errorAlert(data.name)
                        }else if (data.email != null){
                            errorAlert(data.email)
                        }else if(data.password != null){
                            errorAlert(data.password)
                        }
                    }
                });
            }
        });

    })

    $('#loginBtn').click(function (){
        let name = $('#name-login').val();
        let password = $('#password-login').val();

        $.ajax({
            url: 'http://localhost:8080/api/v1/auth/authenticate',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                "email": name,
                "password": password
                // "name": "USER",
                // "role": "USER"
            }),
            success: function(response) {
                localStorage.setItem("token", response.data.token);
                Swal.fire({
                    icon: "success",
                    title: "Success",
                    text: "Login Successful!",
                }).then(() => {
                    window.location.href = "home.html";
                });
            },
            error: function (error) {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: " Something went wrong!",
                }).then(() => {
                    window.location.href = "login.html";
                });
            }
        });




    });


    function errorAlert(message){
        showAlert("error","Oops...",message)
    }
});