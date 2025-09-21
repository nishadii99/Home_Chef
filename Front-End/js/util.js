$(document).ajaxStart(function () {
    $("#loading").fadeIn();
}).ajaxStop(function () {
    $("#loading").fadeOut();
})

function showAlert(icon,title,text) {
    Swal.fire({
        icon: icon,
        title: title,
        text: text,
        timer: 3000,
        backdrop: "rgba(0,0,0,0.8)",
    });
}


function showAlertThenRedirect(icon,title,text,url) {
    Swal.fire({
        icon: icon,
        title: title,
        text: text,
        timer: 2000,
        backdrop: "rgba(0,0,0,0.8)",
    }).then(() => {
        window.location.href = url; // Using the utility function for redirection
    });

}

function logout() {
    localStorage.removeItem('token');
    window.location.href = 'index.html';
}

function loadProfile(role) {
    //check if token is present
    const token = localStorage.getItem('token')
    if (token === null) {
        window.location.href = 'index.html';
    } else {
        //get user profile
        $.ajax({
            url: `http://localhost:8080/api/v1/${role}/profile`,
            type: 'GET',
            headers: {"Authorization": "Bearer " + token},
            success: function (res) {
                let name = res.data.name;
                $('#userName').text(name);
                // $('#profileLogo').attr('src', res.data.profileImage);
                $('#profileName').text(name);

            },
            error: function (error) {
                showAlertThenRedirect(
                    "error",
                    "Oops...",
                    "Something went wrong!",
                    "index.html")
            }
        })
    }
}

function checkXHR(xhr) {
    if (xhr.status === 0) {
        if (xhr.readyState === 0) {
            // Server is down (Connection refused)
            Swal.fire({
                icon: "warning",
                title: "Warning!",
                text: "Cannot connect to the server!",
            }).then(() => {
                window.location.href = "404.html";
            })
        } else {
            // Request Blocked (CORS, Firewall, Network issue)
            Swal.fire({
                icon: "warning",
                title: "Request Blocked!",
                text: "The request was blocked. Try logging again!.",
            }).then(() => {
                window.location.href = "index.html";
                // localStorage.removeItem('token');
            })
        }
    }
}