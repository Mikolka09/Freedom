let size = 8;
document.addEventListener('click', function (event) {
    if ([...event.target.classList].includes("pb")) {
        let number = event.target.textContent;
        $("a.active").removeClass("active");
        event.target.classList.add('active');
        gettingPosts(number);
    }
});

document.getElementById("prev").addEventListener('click', function () {
    let button = document.getElementsByClassName("active")[0];
    let number = parseInt(button.innerHTML) - 1;
    if (number > 0) {
        $("a.active").removeClass("active");
        let pbs = document.getElementsByClassName("pb");
        for (let i = 0; i < pbs.length; i++) {
            if (parseInt(pbs[i].innerText) === number)
                pbs[i].classList.add("active");
        }
        gettingPosts(number);
    }
});

document.getElementById("next").addEventListener('click', function () {
    let button = document.getElementsByClassName("active")[0];
    let number = parseInt(button.innerHTML) + 1;
    let pbs = document.getElementsByClassName("pb");
    if (number - 1 < pbs.length) {
        $("a.active").removeClass("active");
        for (let i = 0; i < pbs.length; i++) {
            if (parseInt(pbs[i].innerText) === number)
                pbs[i].classList.add("active");
        }
        gettingPosts(number);
    }
});

function gettingPosts(number) {
    let start = size * (number - 1);
    let end = size * number;
    let text = $('#content-posts').data('text');
    $.get({
        url: '/api/posts/search/' + text,
        success: (data) => {
            printPosts(data.slice(start, end));
        },
        error: (err) => {
            console.log(err);
        }
    });
}

function printPosts(data) {
    let posts = document.getElementById("content-posts");
    posts.innerHTML = "";
    for (let i = 0; i < data.length; i++) {
        let div1 = document.createElement("div");
        div1.className = "d-md-flex post-entry-2 small-img";
        let a1 = document.createElement("a");
        a1.className = "me-4 thumbnail";
        let img1 = document.createElement("img");
        img1.className = "img-fluid";
        img1.alt = "Img";
        img1.src = "/" + data[i].imgUrl;
        a1.appendChild(img1);
        div1.appendChild(a1);
        let div2 = document.createElement("div");
        let div3 = document.createElement("div");
        div3.className = "post-meta";
        let span1 = document.createElement("span");
        span1.className = "date";
        span1.innerText = data[i].category.name;
        div3.appendChild(span1);
        let span2 = document.createElement("span");
        span2.className = "mx-1";
        span2.innerText = ".";
        div3.appendChild(span2);
        let span3 = document.createElement("span");
        span3.innerText = correctDate(data[i].createdAt);
        div3.appendChild(span3);
        div2.appendChild(div3);
        let h1 = document.createElement("h3");
        let a2 = document.createElement("a");
        a2.href = "/view-post/" + data[i].id;
        a2.innerText = data[i].title;
        h1.appendChild(a2);
        div2.appendChild(h1);
        let p1 = document.createElement("p");
        p1.innerText = data[i].shortDescription;
        div2.appendChild(p1);
        let div4 = document.createElement("div");
        div4.className = "d-flex align-items-center author";
        let div5 = document.createElement("div");
        div5.className = "photo";
        let img2 = document.createElement("img");
        img2.className = "img-fluid";
        img2.alt = "avatar";
        img2.src = "/" + data[i].user.avatarUrl;
        div5.appendChild(img2);
        div4.appendChild(div5);
        let div6 = document.createElement("div");
        div6.className = "name";
        let h2 = document.createElement("h3");
        h2.className = "m-0 p-0";
        h2.innerText = data[i].user.fullName;
        div6.appendChild(h2);
        div4.appendChild(div6);
        let div7 = document.createElement("div");
        div7.className = "row mx-2";
        let div8 = document.createElement("div");
        div8.className = "col-sm-6";
        let img3 = document.createElement("img");
        img3.id = "img-like";
        img3.src = "/img/icon/like.png";
        img3.width = 25;
        div8.appendChild(img3);
        let span4 = document.createElement("span");
        span4.className = "px-1";
        span4.id = "likes";
        span4.style.fontWeight = "bold";
        span4.style.color = "blueviolet";
        span4.innerText = data[i].likes == null ? "0" : data[i].likes;
        div8.appendChild(span4);
        div7.appendChild(div8);
        let div9 = document.createElement("div");
        div9.className = "col-sm-6";
        let img4 = document.createElement("img");
        img4.id = "img-comment";
        img4.src = "/img/icon/comment.png";
        img4.width = 25;
        div9.appendChild(img4);
        let span5 = document.createElement("span");
        span5.className = "px-1";
        span5.id = "comments-post";
        span5.style.fontWeight = "bold";
        span5.style.color = "blueviolet";
        span5.innerText = data[i].comments.length == null ? "0" : data[i].comments.length;
        div9.appendChild(span5);
        div7.appendChild(div9);
        div4.appendChild(div7);
        div2.appendChild(div4);
        div1.appendChild(div2);
        posts.appendChild(div1);
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric'
    }).replace(/ /g, ' ');
}