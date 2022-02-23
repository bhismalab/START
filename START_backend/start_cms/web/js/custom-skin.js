$(document).ready(function() {
	// customized file inputs placeholder updating on change
	var file_api = ((window.File && window.FileReader && window.FileList && window.Blob)? true: false);
	$(document).on('change', '.customizedFileInputBox input[type="file"]', function() {
		var $el = $(this);
		var $context = $el.parent();
		var file_name = '';
		if (file_api && this.files[0]) {
			file_name = this.files[0].name;
		} else {
			file_name = $(this).val().replace("C:\\fakepath\\", '');
		}

		if (file_name) {
			$('.customizedFileInput', $context).text(file_name);
		} else {
			$('.customizedFileInput', $context).text($(this).attr('placeholder'));
		}
	});

	$(document).on('click', '.sidebar-toggle', function(e) {
		var is_menu_collapsed = $('body').hasClass('sidebar-collapse');

		$.ajax({
			url: '?controller=base&action=save_menubar_status',
			data: {
				is_menu_collapsed: (is_menu_collapsed? '1': '0'),
				CSRF_token: $('meta[name="csrf-token"]').attr('content')
			},
			async: true,
			cache: false,
			method: 'post',
			dataType: 'json',
			success: function(response, status, xhr) {
				if (response.success) {
					console.log('Menu bar status successfully saved');
				} else {
					console.log('Cannot save menu bar status');
				}
			},
			error: function(xhr, err, descr) {
				console.log('Cannot save menu bar status');
			}
		});
	});
});