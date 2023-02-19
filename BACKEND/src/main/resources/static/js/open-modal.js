$('#DeleteUserModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let action = "/user/delete/" + id;
    let label = "Are you sure you want to delete your account?";
    let text = "Select \"Delete\" below if you are ready to remove your account"
    let modal = $(this);
    modal.find('#deleteModalLabel').text(label);
    modal.find('#text').text(text);
    modal.find('#action').prop("href", action);
})

$('#AlertModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let name = button.data('name');
    let alert = button.data('text');
    let date = button.data('date');
    let actionConfirm = "/user/alerts/confirm/" + id;
    let actionDeny = "/user/alerts/deny/" + id;
    let modal = $(this);
    modal.find('#alertModalLabel').text(name);
    modal.find('#text').text(alert);
    modal.find('#date-alert').text(date);
    modal.find('#actionConfirm').prop("href", actionConfirm);
    modal.find('#actionDeny').prop("href", actionDeny);
    changeStatusAlert(id);
})

function changeStatusAlert(id) {
    $.get({
        url: 'user/alerts/change/' + id,
        success: (data) => {
            console.log(data);
            printAlerts(data);
        },
        error: (err) => {
            console.log("Error: " + err);
        }
    });
}

function printAlerts(data) {
    if (data != null) {
        let container = document.getElementById('list-alerts');
        let h1 = document.createElement('h6');
        h1.className = "dropdown-header";
        h1.innerText = "Alerts Center";
        container.innerHTML = "";
        container.appendChild(h1);
        for (let i = 0; i < 3; i++) {
            let a = document.createElement('a');
            a.className = "dropdown-item d-flex align-items-center";
            a.dataset.id = data[i].id;
            a.dataset.name = data[i].invite.userFrom.fullName;
            a.dataset.text = data[i].text;
            a.dataset.target = "#AlertModal";
            a.dataset.toggle = "modal";
            a.dataset.date = correctDate(data[i].createdAt);
            a.href = "#";
            let div1 = document.createElement('div');
            div1.className = "mr-3";
            let div2 = document.createElement('div');
            div2.className = "icon-circle bg-warning";
            let i1 = document.createElement('i');
            i1.className = "fas fa-exclamation-triangle text-white";
            div2.appendChild(i1);
            div1.appendChild(div2);
            a.appendChild(div1);
            let div3 = document.createElement('div');
            div3.style.fontWeight = "normal";
            let div4 = document.createElement('div');
            div4.className = "small text-gray-500";
            div4.innerText = correctDate(data[i].createdAt);
            div3.appendChild(div4);
            let span = document.createElement('span');
            span.id = "text-alert";
            span.innerText = data[i].text;
            span.style.fontWeight = data[i].invite.status === "VIEWED" ? "normal" : "bold";
            div3.appendChild(span);
            a.appendChild(div3);
            container.appendChild(a);
        }
        let a2 = document.createElement('a');
        a2.className = "dropdown-item text-center small text-gray-500";
        a2.href = "/user/alerts";
        a2.innerText = "Show All Alerts";
        container.appendChild(a2);
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'long', year: 'numeric'
    }).replace(/ /g, ' ');
}