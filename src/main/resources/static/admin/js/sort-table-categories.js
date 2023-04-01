$('.sort-table-category').on('click', function (e) {
    let categories = data;
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Name":
            img = $('#name');
            if (tag === "down") {
                let data = categories.sort((x, y) => x.name.localeCompare(y.name));
                printTableCategory(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = categories.sort((x, y) => y.name.localeCompare(x.name));
                printTableCategory(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Status":
            img = $('#stat');
            if (tag === "down") {
                let data = categories.sort((x, y) => x.status.localeCompare(y.status));
                printTableCategory(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = categories.sort((x, y) => y.status.localeCompare(x.status));
                printTableCategory(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "CreatedAt":
            img = $('#crt');
            if (tag === "down") {
                let data = categories.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableCategory(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = categories.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableCategory(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "UpdatedAt":
            img = $('#urt');
            if (tag === "down") {
                let data = categories.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTableCategory(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = categories.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTableCategory(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableCategory(data) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = data[i].imgUrl;
            img1.width = 100;
            img1.alt = "image";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            td2.innerText = data[i].name;
            let td3 = document.createElement('td');
            td3.innerText = data[i].shortDescription;
            let td4 = document.createElement('td');
            td4.innerText = data[i].status;
            let td5 = document.createElement('td');
            td5.innerText = correctDate(data[i].createdAt);
            let td6 = document.createElement('td');
            td6.innerText = correctDate(data[i].updatedAt);
            let td7 = document.createElement('td');
            let a1 = document.createElement('a');
            a1.href = "#";
            a1.title = "Edit";
            a1.style.marginRight = "4px";
            a1.dataset.id = data[i].id;
            a1.dataset.name = data[i].name;
            a1.dataset.short = data[i].shortDescription;
            a1.dataset.img = data[i].imgUrl;
            a1.dataset.toggle = "modal";
            a1.dataset.target = "#EditModal";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/edit.png";
            img2.width = 30;
            a1.appendChild(img2);
            td7.appendChild(a1);
            let a2 = document.createElement('a');
            a2.hidden = data[i].status !== "DELETED";
            a2.href = "#";
            a2.title = "Recovery";
            a1.dataset.id = data[i].id;
            a1.dataset.name = data[i].name;
            a1.dataset.short = data[i].shortDescription;
            a1.dataset.img = data[i].imgUrl;
            a2.dataset.toggle = "modal";
            a2.dataset.target = "#RecoveryModal";
            let img3 = document.createElement('img');
            img3.src = "/img/icon/restore.png";
            img3.width = 30;
            a2.appendChild(img3);
            td7.appendChild(a2);
            let a3 = document.createElement('a');
            a3.hidden = data[i].status === "DELETED";
            a3.href = "#";
            a3.title = "Delete";
            a3.dataset.id = data[i].id;
            a3.dataset.name = data[i].name;
            a3.dataset.toggle = "modal";
            a3.dataset.target = "#DeleteModal";
            let img4 = document.createElement('img');
            img4.src = "/img/icon/delete.png";
            img4.width = 30;
            a3.appendChild(img4);
            td7.appendChild(a3);

            tr.append(td1, td2, td3, td4, td5, td6, td7);
            container.append(tr);
        }
    }
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    if (text !== "") {
        input.val('');
        searchCategory(text);
    }
})

function searchCategory(text) {
    let categories = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < categories.length; i++) {
            if (categories[i].name.toLowerCase().includes(txt.toLowerCase()) ||
               searchWorldToString(categories[i].shortDescription.toLowerCase(), txt.toLowerCase()) ||
                categories[i].status.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(categories[i]);
                else {
                    if (result.filter(x => x.id === categories[i].id).length === 0)
                        result.push(categories[i]);
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableCategory(result);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}
