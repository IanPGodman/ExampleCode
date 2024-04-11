

function getContent(contentSrc)
{
    jQuery.ajax({
        type: "GET",
        url: contentSrc,
        success: function(data) {
            jQuery('#ajax-block').html(data);
        }
    });
}

function vegPress(veg){
    jQuery.ajax({
        url: "add/" + veg,
        cache: false,
        success: function (result) {
            jQuery("#havelist").html(result);
        }
    });
}

function vegRemove(veg){
    jQuery('<div id="remove-veg-dialog"></div>').html("Remove: '" + veg + "'").dialog({
        title: 'Question?',
        resizable: false,
        modal: true,
        buttons: {
            'Yes': function () {
                jQuery.ajax({
                    url: "remove/" + veg,
                    cache: false,
                    success: function (result) {
                        console.log(result)
                        jQuery("#admin-ajax-panel").html(result);
                        removeDialog = jQuery("#remove-veg-dialog")
                        removeDialog.dialog('close');
                        removeDialog.remove();
                    }
                });
            },
            'No!': function () {
                jQuery(this).dialog('close');
            }
        }
    });
}

function clearVegSel(){
    jQuery.ajax({
        url: "clear",
        cache: false,
        success: function (result) {
            jQuery("#havelist").html(result);
        }
    });
}

function logout(){
    jQuery.ajax({
        type: "POST",
        url: "logout",
        cache: false,
        success: function () {
            window.location.href="/"
        }
    });
}

function recipeSugestions(){
    jQuery.ajax({
        type: "GET",
        url: "recipe",
        cache: false,
        success: function (result) {
            const obj = JSON.parse(result);
            if (obj.error) {
                jQuery('<div></div>').html(obj.error).dialog({
                    title: 'Error!',
                    resizable: false,
                    modal: true,
                    buttons: {
                        'Ok': function () {
                            jQuery(this).dialog('close');
                        }
                    }
                });
            }
            else{
                window.location.href="/"+obj.success
            }
        }
    });
}

function extractNumber(inputString) {
    const regex = /^.*?-(\d+)/;
    const match = inputString.match(regex);
    if (match) {
        return parseInt(match[1], 10); // Extracted number
    }
    return null; // No valid match found
}

jQuery(function () {
    var token = jQuery("meta[name='_csrf']").attr("content")
    var header = jQuery("meta[name='_csrf_header']").attr("content");
    jQuery(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

function userUpdate(element){
    property = "none"
    if (element.id.startsWith("admin")) {
        property = "admin"
    }
    else if (element.id.startsWith("enabled")) {
        property = "enabled"
    }

    dataStr = JSON.stringify({ 'userid': extractNumber(element.id), 'prop' : property,  'enable': element.checked});

    jQuery.ajax({
        type: "PUT",
        url: "updateuser",
        contentType: 'application/json',
        data: dataStr,
        success: function (result) {
            jQuery('<div></div>').html(result).dialog({
                title: 'Success!',
                resizable: false,
                modal: true,
                buttons: {
                    'Ok': function () {
                        jQuery(this).dialog('close');
                        jQuery(this).remove()
                    }
                }
            });
        }
    });
}

function removeVeg(){
    jQuery.ajax({
        url: "removeveg",
        type: "GET",
        cache: false,
        success: function (result) {
            jQuery("#admin-ajax-panel").html(result);
        }
    });
}

function addNewVeg(){
    jQuery.ajax({
        type: "GET",
        url: "addveg",
        cache: false,
        success: function (result) {
            jQuery("#admin-ajax-panel").html(result);
        }
    });
}


function addVeg(form){
    var formData = new FormData(form);

    jQuery.ajax({
        url: '/addnewveg',
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            console.log('Form submitted successfully:', response);
            jQuery('<div></div>').html(response).dialog({
                title: 'Success!',
                resizable: false,
                modal: true,
                buttons: {
                    'Close': function () {
                        jQuery(this).dialog('close');
                        jQuery(this).remove()
                    }
                }
            });
        },
        error: function (xhr, status, error) {
            if (xhr.status == 413){
                error = "Error file too large!"
            }
            else{
                error = xhr.responseText;
            }
            console.error('Error submitting form: %s', error);
            jQuery('<div></div>').html(error).dialog({
                title: 'ERROR!',
                resizable: false,
                modal: true,
                buttons: {
                    'Close': function () {
                        jQuery(this).dialog('close');
                        jQuery(this).remove()
                    }
                }
            });
        }
    });
    return false;
}



