$('.sort-table-user').on('click', function (){
    let users = data;
    let column = $(this);
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name){
        case "Username":
            if(tag==="down") {
                let data = users.sort((x, y) => x.username.localeCompare(y.username));
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => y.username.localeCompare(x.username));
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "FullName":
            if(tag==="down") {
                let data = users.sort((x, y) => x.fullName.localeCompare(y.fullName));
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => y.fullName.localeCompare(x.fullName));
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "Age":
            if(tag==="down") {
                let data = users.sort((x, y) => x.age - y.age);
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => y.age - x.age);
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "Email":
            if(tag==="down") {
                let data = users.sort((x, y) => x.email.localeCompare(y.email));
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => y.email.localeCompare(x.email));
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "Rating":
            if(tag==="down") {
                let data = users.sort((x, y) => x.rating - y.rating);
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => y.rating - x.rating);
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "CreatedAt":
            if(tag==="down") {
                let data = users.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        case "UpdatedAt":
            if(tag==="down") {
                let data = users.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTableUsers(data);
                column.attr("data-tag", "up");
            }else{
                let data = users.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTableUsers(data);
                column.attr("data-tag", "down");
            }
            break;
        default:
            break;
    }
})

function printTableUsers(data){
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if(data!=null){
        for(let i=0; i<data.length; i++){
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = "/" + data[i].avatarUrl;
            img1.style.borderRadius = "50%";
            img1.style.paddingBottom = "5px";
            img1.width = 50;
            img1.alt = "avatar";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            td2.innerText = data[i].username;
            let td3 = document.createElement('td');
            td3.innerText = data[i].fullName==null?"NULL":data[i].fullName;
            let td4 = document.createElement('td');
            td4.innerText = data[i].age;
            let td5 = document.createElement('td');
            td5.innerText = data[i].email;
            let td6 = document.createElement('td');
            td6.innerText = data[i].status;
            let td7 = document.createElement('td');
            td7.innerText = data[i].rating;
            let td8 = document.createElement('td');
            td8.innerText = correctDate(data[i].createdAt);
            let td9 = document.createElement('td');
            td9.innerText = correctDate(data[i].updatedAt);
            let td10 = document.createElement('td');
            let a2 = document.createElement('a');
            a2.href= "/admin/users/view/"+data[i].id;
            a2.title = "View";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/view.png";
            img2.width = 30;
            a2.appendChild(img2);
            td10.appendChild(a2);
            let a3 = document.createElement('a');
            a3.href= "/admin/users/edit/"+data[i].id;
            a3.title = "Edit";
            let img3 = document.createElement('img');
            img3.src = "/img/icon/edit.png";
            img3.width = 30;
            a3.appendChild(img3);
            td10.appendChild(a3);
            let a4 = document.createElement('a');
            a4.href= "/admin/users/newpass/"+data[i].id;
            a4.title = "Edit Password";
            let img4 = document.createElement('img');
            img4.src = "/img/icon/password.png";
            img4.width = 30;
            a4.appendChild(img4);
            td10.appendChild(a4);
            let a5 = document.createElement('a');
            a5.href= "#";
            a5.title = "Delete";
            a5.dataset.id = data[i].id;
            a5.dataset.name = data[i].username;
            a5.dataset.toggle = "modal";
            a5.dataset.target = "#DeleteModal";
            let img5 = document.createElement('img');
            img5.src = "/img/icon/delete.png";
            img5.width = 30;
            a5.appendChild(img5);
            td10.appendChild(a5);
            tr.append(td1, td2, td3, td4, td5, td6, td7, td8, td9, td10);
            container.append(tr);
        }
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'long', year: 'numeric'
    }).replace(/ /g, ' ');
}