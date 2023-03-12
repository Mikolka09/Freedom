$('.sort-table-inbox').on('click', function () {
    let senders = Object.values(list);
    console.log(senders)
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Senders":
            img = $('#send');
            if (tag === "down") {
                let data = senders.sort((x, y) => x[0].invite.userFrom.fullName.localeCompare(y[0].invite.userFrom.fullName));
                printAllSenders(editArrToTree(data), data[0][0].invite.userFrom.fullName);
                printAllUserMessages(senders[0]);
                searchMess = senders;
                searchSend = editArrToTree(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = senders.sort((x, y) => y[0].invite.userFrom.fullName.localeCompare(x[0].invite.userFrom.fullName));
                printAllSenders(editArrToTree(data), data[0][0].invite.userFrom.fullName);
                printAllUserMessages(senders[0]);
                searchMess = senders;
                searchSend = editArrToTree(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function editArrToTree(data) {
    let arr = [];
    for (let st of data) {
        let t = [];
        t.push(st);
        arr.push(t);
    }
    return arr;
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let mess = document.location.href.includes("messages");
    let del = document.location.href.includes("deleted");
    let out = document.location.href.includes("out");
    let text = input.val();
    let list = $("#table-list").attr("data-table");
    if (text !== "") {
        input.val('');
        if (mess && !del && !out) {
            searchMails(text);
        } else
            searchEmails(text, list);
    }
})

function searchMails(text) {
    let senders = Object.values(list);
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let send of senders) {
            let test = [];
            for (let arr of send) {
                if (arr.invite.userFrom.username.toLowerCase().includes(txt.toLowerCase()) ||
                    arr.invite.userFrom.fullName.toLowerCase().includes(txt.toLowerCase())) {
                    if (test.length === 0)
                        test.push(arr);
                    else {
                        if (test.filter(x => x.id === arr.id).length === 0)
                            test.push(arr);
                    }
                } else if (searchWorldToString(arr.message.toLowerCase(), txt.toLowerCase())) {
                    if (test.length === 0)
                        test.push(arr);
                    else {
                        if (test.filter(x => x.id === arr.id).length === 0)
                            test.push(arr);
                    }
                }
            }
            if (test.length !== 0) {
                if (result.length === 0)
                    result.push(test);
                else {
                    let count = 0;
                    for (let res of result) {
                        if (res.filter(x => x.id === test[0].id).length !== 0)
                            count++;
                    }
                    if (count === 0)
                        result.push(test);
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        searchMess = result;
        searchSend = editArrToTree(result);
        alertInfo(answer);
        printAllSenders(editArrToTree(result), result[0][0].invite.userFrom.fullName);
        printAllUserMessages(result[0]);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}
