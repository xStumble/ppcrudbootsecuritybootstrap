const buttons = document.querySelectorAll('.editbutton, .deletebutton');

if (buttons.length > 0) {
    buttons.forEach((element) => {
        element.addEventListener("click", function() {
            const modal = element.classList.contains('editbutton') 
                ? document.getElementById('editModal') : document.getElementById('deleteModal');

            const tr = element.closest('tr');
            const trChildren = tr.children;
            const modalInputs = modal.getElementsByTagName('input');
            for (let i = 1; i < trChildren.length - 2; i++) {
                modalInputs[i].value = trChildren[i-1].innerText;
            }

            const select = modal.getElementsByTagName('select')[0];
            const userRoles = tr.children[6].innerText.split(' ');
            Array.from(select.options).forEach((option) => {
               option.selected = false;
               userRoles.forEach((userRole) => {
                  if (option.innerText === userRole) {
                      option.selected = true;
                  }
               });
            });

            const id = tr.children[0].innerText;
            const regex = /id=\d+/gm;
            const form = modal.getElementsByTagName('form')[0];
            form.action = form.action.replace(regex, 'id=' + id);
        });
    });
}