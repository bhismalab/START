function Utils() {
	var config = {
		img_dir: 'web/images/'
	};

	this.alert = function(message, callback) { // deprecated, use bootbox.alert() instead
		if (!$.fancybox) {
			alert(message);
			return;
		}
		var has_callback = (typeof(callback)=='function');
		var ok_id = 'confirm_'+this.generatePassword();
		$.fancybox.open({
			content: '<div style="width: 420px;"><h4 class="popupTitle">'+t['js_alert']+'</h4><p class="popupDescription">'+this.safeHtmlString(message)+'</p><div class="popupControls"><a class="btn btn-primary" id="'+ok_id+'"><i class="fa fa-check" aria-hidden="true"></i> '+t['js_ok']+'</a></div></div>'
		});
		$('#'+ok_id).click(function() {
			$.fancybox.close();
			if (has_callback) {callback();}
			return false;
		});
	};

	this.setConfirmation = function(event_type, element, message, callback) { // deprecated, use bootbox.confirm() instead
		var $el = $(element);
		var has_callback = (typeof(callback)=='function');
		var ok_id = 'confirm_'+this.generatePassword();
		var cancel_id = 'decline_'+this.generatePassword();
		$el.on(event_type, function(event) {
			// check is element is already confirmed
			// if confirmed do not prevent default
			// if not confirmed
			//  show popup
			//  on popup confirmation
			//   call function that marks element as confirmed
			//  prevent default
			if (!$el.attr('data-confirmed') || has_callback) {
				event.preventDefault();
				$.fancybox.open({
					content: '<div style="width: 420px;"><h4 class="popupTitle">'+t['js_confirmation']+'</h4><p class="popupDescription">'+utils.safeHtmlString(message)+'</p><div class="popupControls"><a class="btn btn-danger" id="'+ok_id+'"><i class="fa fa-check" aria-hidden="true"></i> '+t['js_confirm']+'</a> <a class="btn btn-primary" id="'+cancel_id+'" onclick="$.fancybox.close(); return false;"><i class="fa fa-times" aria-hidden="true"></i> '+t['js_cancel']+'</a></div></div>'
				});
				$('#'+ok_id).click(function() {
					$.fancybox.close();
					if (has_callback) {
						callback($el);
					} else {
						$el.attr('data-confirmed', 'confirmed');
						if ((event_type=='click') && $el.is('a')) {
							$el.get(0).click();
							console.log($el.get(0))
						} else {
							$el.trigger(event_type);
						}
					}
					return false;
				});
			}
		});
	};

	this.htmlEntities = function(str) {
		var div = document.createElement('pre');
		var text = document.createTextNode(str);
		div.appendChild(text);
		return div.innerHTML;
	};

	this.isElement = function(elem) {
		if ((elem instanceof jQuery && elem.length) || elem instanceof HTMLElement) {
			return true;
		}
		return false;
	};

	this.generatePassword = function(opts) {
		var length = 8;
		if (opts && opts.length) {length = opts.length;}
		var mode = 'light';
		if (opts && opts.mode) {
			if (opts.mode=='default') {mode = 'default';}
		}
		var use_sets = ['string', 'numeric'];
		if (opts && opts.use_sets && opts.use_sets.length) {
			use_sets = opts.use_sets;
		}

		var sets = {
			'uppercase': {
				'default': 'ABCDEFGHIJKLNOPQRSTUVWXYZ',
				'light': 'ABCDEFGHJKNPQRSTUVWXYZ'
			},
			'string': {
				'default': 'abcdefghijklnopqrstuvwxyz',
				'light': 'abcdefghijknpqrstuvwxyz'
			},
			'numeric': {
				'default': '0123456789',
				'light': '23456789'
			},
			'punctuation': {
				'default': '!@#$%^&*()_+~`|}{[]\:;?><,./-=',
				'light': '!@#$^&*_~;?-'
			}
		};
		var symbols_allowed = '';
		var set = '';
		for (set in use_sets) {
			if (sets[use_sets[set]]) {
				symbols_allowed+=sets[use_sets[set]][mode];
			}
		}

		var pwd = '';
		while (pwd.length<length) {
			pwd+=symbols_allowed.charAt(Math.floor(Math.random()*symbols_allowed.length));
		}

		return pwd;
	};

	this.nl2br = function(str) {
		return str.replace(/([^>])\n/g, '$1<br/>');
	};

	this.safeHtmlString = function(str) {
		return this.nl2br(this.htmlEntities(str));
	};

	this.strtr = function(str, from, to) {
		/* strtr by Kedo 2009 */
		if (typeof from==='object') {
			var cmpStr = '';
			for (var j=0; j<str.length; j++) {
				cmpStr+='0';
			}
			var offset = 0;
			var find = -1;
			var addStr = '';
			for (fr in from) {
				offset = 0;
				while ((find = str.indexOf(fr, offset)) != -1) {
					if (parseInt(cmpStr.substr(find, fr.length)) != 0) {
						offset = find + 1;
						continue;
					}
					for (var k =0 ; k < from[fr].length; k++){
						addStr+='1';
					}
					cmpStr = cmpStr.substr(0, find) + addStr + cmpStr.substr(find + fr.length, cmpStr.length - (find + fr.length));
					str = str.substr(0, find) + from[fr] + str.substr(find + fr.length, str.length - (find + fr.length));
					offset = find + from[fr].length + 1;
					addStr = '';
				}
			}
			return str;
		}

		for (var i = 0; i < from.length; i++) {
			str = str.replace(new RegExp(from.charAt(i),'g'), to.charAt(i));
		}

		return str;
	};
}
var utils = new Utils();