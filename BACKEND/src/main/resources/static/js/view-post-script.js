$('#likes-button').on('click', function () {
    let postId = $('#likes-button').data('id');
    $('#likes-button').off('click');
    if (postId) {
        $.get({
            url: '/api/posts/likes/' + postId,
            success: (data) => {
                $('#likes').text(data.likes)
            },
            error: (err) => {
                console.log(err);
            }
        });
        $('#img-like-do').attr('src', '/img/icon/like.png');
    }
});

$('#modalAbandonedCart').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let idTo = button.data('id');
    let name = button.data('name');
    let posts = button.data('posts');
    let comments = button.data('comments');
    let friends = button.data('friends');
    let friend = button.data('friend');
    let idFrom = button.data('user');
    let rating = button.data('rating');
    let avatar = "/" + button.data('avatar');
    let modal = $(this);
    modal.find('#userFrom').val(idFrom);
    modal.find('#userTo').val(idTo);
    modal.find('#name-user').text(name);
    modal.find('#posts-user').text(posts);
    modal.find('#comments-user').text(comments);
    modal.find('#friends-user').text(friends);
    modal.find('#rating-user').text(rating);
    modal.find('#avatar').attr('src', avatar);
    if (typeof idFrom == "undefined") {
        modal.find('#message').attr('disabled', true);
        modal.find('#follow').attr('disabled', true);
    } else if (parseInt(idFrom) === parseInt(idTo)) {
        modal.find('#message').attr('disabled', true);
        modal.find('#follow').attr('disabled', true);
    }else if (friend==="yes") {
        modal.find('#follow').attr('disabled', true);
        $('#btn-follow').attr('title', 'You are already friends');
    }
});

$('#follow').on('click', function (e) {
    let userFrom = $('#userFrom').val();
    let userTo = $('#userTo').val();
    $.get({
        url: '/user/alerts/follow/' + userTo,
        data: {
            userId: userFrom
        },
        success: (data) => {
            console.log(data);
            if (data === "OK") {
                let text = "Friend request sent!";
                answerModal(text);
            } else {
                let text = "A friend request has already been sent. Wait for a response from the user!";
                answerModal(text);
            }

        },
        error: (err) => {
            console.log(err);
        }
    });
});

function answerModal(text) {
    $('#answer-text').text(text);
    new bootstrap.Modal(document.getElementById("answerModal")).show();
}

$('#comment-button').on('click', function (e) {
    e.preventDefault();
    let userId = $('#userId').val();
    let postId = $('#postId').val();
    let text = $('#comment-message').val();
    if (text !== "") {
        $.get({
            url: '/api/posts/comment/' + postId,
            data: {
                userId: userId,
                text: text
            },
            success: (data) => {
                printComments(data);
            },
            error: (err) => {
                console.log(err);
            }
        });
        $('#comment-name').val("");
        $('#comment-email').val("");
        $('#comment-message').val("");
    }
});

function printComments(data) {
    if (data != null) {
        let size = document.getElementById("size");
        if (typeof data.length !== 'undefined')
            size.innerText = data.length;
        let comments = document.getElementById("comments-container");
        comments.innerHTML = "";
        for (let i = 0; i < data.length; i++) {
            let div1 = document.createElement("div");
            let div2 = document.createElement("div");
            div2.className = "comment d-flex mb-4";
            let div3 = document.createElement("div");
            div3.className = "flex-shrink-0";
            let div4 = document.createElement("div");
            div4.className = "avatar avatar-sm rounded-circle";
            let img = document.createElement("img");
            img.className = "avatar-img img-fluid";
            img.alt = "avatar";
            img.src = "/" + data[i].user.avatarUrl;
            div4.appendChild(img);
            div3.appendChild(div4);
            let div5 = document.createElement("div");
            div5.className = "flex-grow-1 ms-2 ms-sm-3";
            let div6 = document.createElement("div");
            div6.className = "comment-meta d-flex align-items-baseline";
            let h1 = document.createElement("h6");
            h1.className = "me-2";
            h1.innerText = data[i].user.fullName;
            let span1 = document.createElement("span");
            span1.className = "text-muted";
            span1.innerText = correctDate(data[i].createdAt);
            div6.appendChild(h1);
            div6.appendChild(span1);
            let div7 = document.createElement("div");
            div7.className = "img-comment-text";
            div7.innerText = data[i].body;
            div5.appendChild(div6);
            div5.appendChild(div7);
            div2.appendChild(div3);
            div2.appendChild(div5);
            div1.appendChild(div2);
            comments.appendChild(div1);
        }
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'long', year: 'numeric'
    }).replace(/ /g, ' ');
}
