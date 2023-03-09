$('.sort-table-tag').on('click', function (e) {
    let posts = data;
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Name":
            img = $('#name');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.name.localeCompare(y.name));
                printTableTags(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.name.localeCompare(x.name));
                printTableTags(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Status":
            img = $('#stat');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.status.localeCompare(y.status));
                printTableTags(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.status.localeCompare(x.status));
                printTableTags(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "CreatedAt":
            img = $('#crt');
            if (tag === "down") {
                let data = posts.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableTags(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableTags(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "UpdatedAt":
            img = $('#urt');
            if (tag === "down") {
                let data = posts.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTableTags(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTableTags(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableTags(data) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            td1.innerText = data[i].name;
            let td2 = document.createElement('td');
            td2.innerText = data[i].status;
            let td3 = document.createElement('td');
            td3.innerText = correctDate(data[i].createdAt);
            let td4 = document.createElement('td');
            td4.innerText = correctDate(data[i].updatedAt);
            let td5 = document.createElement('td');
            let a1 = document.createElement('a');
            a1.href = "#";
            a1.title = "Edit";
            a1.dataset.id = data[i].id;
            a1.dataset.name = data[i].name;
            a1.dataset.toggle = "modal";
            a1.dataset.target = "#EditModal";
            let img1 = document.createElement('img');
            img1.src = "/img/icon/edit.png";
            img1.width = 30;
            a1.appendChild(img1);
            td5.appendChild(a1);
            let a2 = document.createElement('a');
            a2.hidden = data[i].status !== "DELETED";
            a2.href = "#";
            a2.title = "Recovery";
            a2.style.marginLeft = "4px";
            a2.dataset.id = data[i].id;
            a2.dataset.name = data[i].name;
            a2.dataset.toggle = "modal";
            a2.dataset.target = "#RecoveryModal";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/restore.png";
            img2.width = 30;
            a2.appendChild(img2);
            td5.appendChild(a2);
            let a3 = document.createElement('a');
            a3.hidden = data[i].status === "DELETED";
            a3.href = "#";
            a3.title = "Delete";
            a3.style.marginLeft = "4px";
            a3.dataset.id = data[i].id;
            a3.dataset.name = data[i].name;
            a3.dataset.toggle = "modal";
            a3.dataset.target = "#DeleteModal";
            let img3 = document.createElement('img');
            img3.src = "/img/icon/delete.png";
            img3.width = 30;
            a3.appendChild(img3);
            td5.appendChild(a3);

            tr.append(td1, td2, td3, td4, td5);
            container.append(tr);
        }
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'short', year: 'numeric'
    }).replace(/ /g, ' ');
}

$("#input-search-admin").keydown(function (event) {
    if (event.keyCode === 13) {
        $("#button-search").click();
        $(this).val('');
    }
});

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    if (text !== "") {
        input.val('');
        searchTag(text);
    }
})

function searchTag(text) {
    let tags = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < tags.length; i++) {
            if (tags[i].name.toLowerCase().includes(txt.toLowerCase()) ||
                tags[i].status.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(tags[i]);
                else {
                    if (result.filter(x => x.id === tags[i].id).length === 0)
                        result.push(tags[i]);
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableTags(result);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}
