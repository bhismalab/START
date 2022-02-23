<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined('_VALID_PHP')) {die('Direct access to this location is not allowed.');}

// print '<pre>'.var_export($child, 1).'</pre>';

?>


<style>
.profile-child-img {
	border-radius: 5%; border: 2px solid #fff; box-shadow: 0 3px 5px rgba(0,0,0,0.3);
}
.parent-signature {
	max-height: 120px; border: 2px solid #8d6fde;
}

#divChildControls a {
	margin-right: 40px;
}
#divChildControls a:last-child {
	margin-right: 0px;
}
</style>


<!-- Deleting hidden form -->
<form action="?controller=children&amp;action=delete" method="post" id="formDeleteItem">
	<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
	<input type="hidden" name="delete" value="0" />
</form>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_children_view_info');?>
		<small><?=utils::safeEcho($child['name'].' '.$child['surname'].' '.$child['patronymic'], 1);?></small>
	</h1>
</section>


<!-- Main content -->
<section class="content">
	<div class="row">
		<div class="col-md-12">
			<nav class="top-toolbar-stripe">
				<a href="<?=utils::safeEcho($link_back, 1);?>" title="<?=CMS::t('back');?>" class="toolbar-btn-primary"><i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i> Back</a>

				<?=view::widget('breadcrumb', ['links' => [
					[
						'title' => 'Children',
						'href' => '?controller=children&action=list',
						'icon' => 'home'
					], [
						'title' => CMS::t('menu_item_children_view_info'),
						'icon' => 'child'
					]
				]]);?>
			</nav>
		</div>
	</div>

	<div class="row">
		<div class="col-md-3">

			<!-- Profile Image -->
			<div class="box box-primary">
				<div class="box-body box-profile">
					<?php if (!empty($child['photo'])) { ?>
					<img src="?controller=<?=$_GET['controller'];?>&amp;action=download_child_photo&amp;child_id=<?=$child['id'];?>" alt="" class="profile-child-img img-responsive" />
					<?php } ?>

					<h3 class="profile-childname text-center"><?=utils::safeEcho($child['name'].' '.$child['surname'].' '.$child['patronymic'], 1);?></h3>

					<!-- <p class="text-muted text-center">Software Engineer</p> -->

					<ul class="list-group list-group-unbordered">
						<?php if (ADMIN_TYPE!='researcher') { ?>
						<li class="list-group-item text-center">

							<?=utils::formatMySQLDate(CMS::$date_format, $child['birth_date']);?>
						</li>
						<?php } ?>
						<li class="list-group-item">
							<b>Survey status:</b>
							<?php
							$survey_status = 'active';
							if ($survey['is_closed']) {
								$survey_status = 'closed';
							} else if ($survey['is_completed']) {
								$survey_status = 'finished';
							}
							?>
							<?=CMS::t('survey_status_'.$survey_status);?>
						</li>
						<li class="list-group-item">
							<b><?=CMS::t('survey_date');?>:</b>
							<time datetime="<?=utils::formatMySQLDate('c', $survey['created_datetime']);?>" title="<?=$survey['created_datetime'];?>"><?=utils::formatMySQLDate('d.m.Y', $survey['created_datetime']);?></time>
						</li>
					</ul>

					<!-- <a href="#" class="btn btn-primary btn-block"><b>Follow</b></a> -->
				</div>
				<!-- /.box-body -->
			</div>
			<!-- /.box (Profile Image) -->

			<div class="box box-default">
				<div class="box-body text-center" id="divChildControls">
					<?php if (CMS::hasAccessTo('children/download_child_survey')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>"><img src="<?=IMAGE_DIR;?>purple-icon-download.png" alt="" /></a>
					<?php } ?>
					<?php if (CMS::hasAccessTo('children/survey')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;return=<?=urlencode($link_back);?>"><img src="<?=IMAGE_DIR;?>purple-icon-survey.png" alt="" /></a>
					<?php } ?>
					<?php if (CMS::hasAccessTo('children/delete')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=delete&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>" id="aDelChild" data-child_id="<?=$child['id'];?>"><img src="<?=IMAGE_DIR;?>purple-icon-trash.png" alt="" /></a>
					<?php } ?>
				</div>
				<!-- /.box-body -->
			</div>

		</div>
		<!-- /.col -->


		<?php if (ADMIN_TYPE!='researcher') { ?>
		<div class="col-md-9">

			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#info" data-toggle="tab">Child's information</a></li>
                    <?php
                    if($child['diagnosis'] != null && $child['diagnosis'] !== ''){ ?>
					<li><a href="#diag" data-toggle="tab"><?=CMS::t('children_profile_diagnosis');?></a></li>
                    <?php } ?>
					<!-- <li><a href="#timeline" data-toggle="tab">Timeline</a></li>
					<li><a href="#activity" data-toggle="tab">Activity</a></li> -->
				</ul>

				<div class="tab-content">

					<!-- Info tab -->
					<div class="active tab-pane" id="info">
						<table class="table table-bordered table-striped">
							<tr>
								<th><?=CMS::t('children_name');?></th>
								<td class="edit" data-field="name-surname">
									<?=utils::safeEcho($child['name'].' '.$child['surname'], 1);?>
								</td>
							</tr><tr>
								<th><?=CMS::t('children_gender');?></th>
								<td class="edit" data-field="gender"><?=(empty($child['gender'])? '': CMS::t('children_gender_'.$child['gender']));?></td>
							</tr><tr>
								<th><?=CMS::t('children_birth_date');?></th>
								<td class="edit" data-field="birth_date"><?=(empty($child['birth_date'])? '': utils::formatMySQLDate(CMS::$date_format, $child['birth_date']));?></td>
							</tr><tr>
								<th>Hand dominance</th>
								<td class="edit" data-field="hand"><?=utils::safeEcho($child['hand'], 1);?></td>
							</tr><tr>
								<th><?=CMS::t('children_age');?></th>
								<td class="calculate-age"><?=utils::getAge($child['birth_date']);?></td>
							</tr><tr>
								<th>Registration date</th>
								<td>
									<time datetime="<?=utils::formatMySQLDate('c', $child['add_datetime']);?>" title="<?=$child['add_datetime'];?>"><?=utils::formatMySQLDate(CMS::$date_format, $child['add_datetime']);?></time>
								</td>
							</tr><tr>
								<th><?=CMS::t('children_state');?></th>
								<td class="edit" data-field="state"><?=utils::safeEcho($child['state'], 1);?></td>
							</tr><tr>
								<th><?=CMS::t('children_address');?></th>
								<td class="edit" data-field="address"><?=utils::safeEcho($child['address'], 1);?></td>
							</tr><?php if (!empty($child['latitude']) && !empty($child['longitude'])) { ?><tr>
								<th><?=CMS::t('GPS');?></th>
								<td>
									<a href="https://maps.google.com/?q=<?=utils::safeEcho($child['latitude'], 1);?>,<?=utils::safeEcho($child['longitude'], 1);?>" target="_blank"><?=utils::safeEcho($child['latitude'].', '.$child['longitude'], 1);?> <i class="fa fa-external-link" aria-hidden="true"></i></a>
								</td>
							</tr><?php } ?>
						</table>
                        <?php if (ADMIN_TYPE!='clinician') { ?>
						<div style="text-align: right;">
							<a class="btn btn-context edit-child" data-child-id="<?=$child['id'];?>" style="margin-bottom: 10px;"><i class="fa fa-edit" aria-hidden="true"></i> Edit child info</a>
						</div>
                        <?php } ?>
					</div>

                    <div class="tab-pane" id="diag">
                        <table class="table table-bordered table-striped">
                            <tr>
                                <th><?=CMS::t('diagnosis');?></th>
                                <td>
                                    <?=$child['diagnosis'];?>
                                </td>
                            </tr><tr>
                                <th><?=CMS::t('diagnosis_clinic');?></th>
                                <td><?=$child['diagnosis_clinic'];?></td>
                            </tr><tr>
                                <th><?=CMS::t('diagnosis_datetime');?></th>
                                <td><?=(empty($child['diagnosis_datetime'])? '': utils::formatMySQLDate('d.m.Y', $child['diagnosis_datetime']));?></td>
                            </tr>
                        </table>
                    </div>
					<!-- /.tab-pane -->

				</div>
				<!-- /.tab-content -->

			</div>
			<!-- /.nav-tabs-custom -->

			<?php if (!empty($parents) && is_array($parents)) { ?>
			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
					<?php foreach ($parents as $i=>$p) { ?>
					<li<?=($i? '': ' class="active"');?>><a href="#parent_<?=$p['id'];?>" data-toggle="tab"><?=ucfirst($p['rel']);?> <?=utils::safeEcho($p['name'].' '.$p['surname'], 1);?></a></li>
					<?php } ?>
					<!-- <li><a href="#timeline" data-toggle="tab">Timeline</a></li>
					<li><a href="#activity" data-toggle="tab">Activity</a></li> -->
				</ul>

				<div class="tab-content">
					<?php foreach ($parents as $i=>$p) { ?>
					<div class="<?=($i? '': 'active ');?>tab-pane parent-info" id="parent_<?=$p['id'];?>">
						<table class="table table-bordered table-striped">
							<tr>
								<th><?=CMS::t($p['rel'].'_ns');?></th>
								<td class="edit" data-field="name-surname">
									<?=utils::safeEcho($p['name'].' '.$p['surname'], 1);?>
								</td>
							</tr><tr>
								<th><?=CMS::t('parent_gender');?></th>
								<td class="edit" data-field="gender">
									<?=CMS::t('parent_gender_'.$p['gender']);?>
								</td>
							</tr><?php if (!empty($p['birth_date'])) { ?><tr>
								<th><?=CMS::t('parent_birth_date');?></th>
								<td class="edit" data-field="birth_date">
									<time datetime="<?=(empty($p['birth_date'])? '': utils::formatMySQLDate('c', $p['birth_date']));?>" title="<?=$p['birth_date'];?>"><?=(empty($p['birth_date'])? '': utils::formatMySQLDate('d.m.Y', $p['birth_date']));?></time>
								</td>
							</tr><tr>
								<th><?=CMS::t('parent_age');?></th>
								<td><?=utils::getAge($p['birth_date']);?></td>
							</tr><?php } ?><tr>
								<th><?=CMS::t('parent_status');?></th>
								<td><?=utils::safeEcho(ucfirst($p['rel']), 1);?></td>
							</tr><tr>
								<th><?=CMS::t('parent_language');?></th>
								<td><?=utils::safeEcho($p['lang'], 1);?></td>
							</tr><tr>
								<th><?=CMS::t('parent_state');?></th>
								<td class="edit" data-field="state"><?=utils::safeEcho($p['state'], 1);?></td>
							</tr><tr>
								<th><?=CMS::t('parent_address');?></th>
								<td class="edit" data-field="address"><?=utils::safeEcho($p['address'], 1);?></td>
							</tr><?php if (!empty($p['preferable_contact'])) { ?><tr>
								<th>Preferable method of contact</th>
								<td><?=utils::safeEcho($p['preferable_contact'], 1);?></td>
							</tr><?php } ?><?php if (!empty($p['phone'])) { ?><tr>
								<th><?=CMS::t('parent_phone');?></th>
								<td><?=utils::safeEcho($p['phone'], 1);?></td>
							</tr><?php } ?><?php if (!empty($p['email'])) { ?><tr>
								<th><?=CMS::t('parent_email');?></th>
								<td><?=utils::safeEcho($p['email'], 1);?></td>
							</tr><?php } ?><?php if (!empty($p['signature'])) { ?><tr>
								<th><?=CMS::t('parent_signature');?></th>
								<td>
									<img src="?controller=parents&amp;action=download_parent_signature&amp;parent_id=<?=$p['id'];?>" alt="" class="img-responsive parent-signature" />
								</td>
							</tr><?php } ?>
						</table>
                        <?php if (ADMIN_TYPE!='clinician') { ?>
						<div style="text-align: right;">
							<a class="btn btn-context edit-parent" data-parent_id="<?=$p['id'];?>" style="margin-bottom: 10px;"><i class="fa fa-edit" aria-hidden="true"></i> Edit parent info</a>
						</div>
                        <?php } ?>
					</div>
					<?php } ?>

				</div>
				<!-- /.tab-content -->

			</div>
			<!-- /.nav-tabs-custom -->
			<?php } ?>

		</div>
		<!-- /.col -->
		<?php } ?>

	</div>
	<!-- /.row -->

</section>
<!-- /.content -->


<script type="text/javascript">

var states = <?php echo json_encode($states); ?>;
var currentStateChild = <?php echo json_encode($child['state']); ?>;
var currentStateParent = <?php echo json_encode($p['state']); ?>;

$('#aDelChild').on('click', function() {
	var child_id = $(this).attr('data-child_id');
    bootbox.confirm({
        message: 'Are you sure you want to delete this child? IMPORTANT: All child\'s survey will be deleted',
        callback: function(ok) {
            if (ok) {
                var $form = $('#formDeleteItem');
                $('[name="delete"]', $form).val('<?=$child['id'];?>');
                $form.submit();
            }
        }
    });

    return false;
});

$("#info").on("click", ".edit-child", function(e) {
    e.stopPropagation();
    $.each($("#info .edit"), function (i, b) {
        var currentValue = $(this).text().trim();

        if ($(this).data("field")=='name-surname') {
            var ns = currentValue.split(" ");
            if (ns.length==2) {
                $(this).html("<input placeholder='name' class=\"update-db\" data-name=\"name\" value = '"+ns[0]+"'><input placeholder='surname' class=\"update-db\" data-name=\"surname\" value='"+ns[1]+"'>");
            } else {
                $(this).html("<input placeholder='name' class=\"update-db\" data-name=\"name\" value = ''><input placeholder='surname' class=\"update-db\" data-name=\"surname\" value=''>");
            }
        } else if ($(this).data("field")=='state') {

			$(this).html("<select class=\"state update-db\" data-name=\"state\"></select>");

			$.each(states, function(key, state) {
				$('.state')
				.append(
					$("<option></option>")
					.attr("value", state.id)
					.prop("selected", currentStateChild === state.name)
					.text(state.name)
				);
			});

		} else {
            $(this).html("<input placeholder='"+$(this).data("field")+"'  class=\"update-db\" data-name='"+$(this).data("field")+"' value = '"+currentValue+"'>");
        }
    });

    /** changing button to "Save" */
    $(this).text("Save child").removeClass("edit-child").addClass("save-child");
});

$("#info").on("click", ".save-child", function(e) {
	var child_id = $(this).data('child-id');
    var to_db = {};
    to_db.CSRF_token = '<?=$CSRF_token;?>';
    $.each($('.update-db'), function () {
        to_db[$(this).data('name')] = $(this).val();
    });

    $.ajax({
        url: '?controller=children&action=edit&child_id='+encodeURIComponent(child_id),
        data: to_db,
        async: true,
        cache: false,
        type: 'post',
        dataType: 'json',
        success: function(response, status, xhr) {
			bootbox.alert("No changes have been made", function() {
					location.href = location.href;
			});
            if (response.success) {
				location.href = location.href;
            }
        },
        error: function(xhr, err, descr) {}
    });
});

$('.parent-info').on('click', '.edit-parent', function(e) {
	var $tab = $(this).closest('.parent-info');

    e.stopPropagation();

    $.each($('.edit', $tab), function (i, b) {
        var currentValue = $(this).text().trim();

        if ($(this).data("field")=='name-surname') {
            var ns = currentValue.split(" ");
            if (ns.length==2) {
                $(this).html("<input placeholder='name' class=\"update-db\" data-name=\"name\" value = '"+ns[0]+"'><input placeholder='surname' class=\"update-db\" data-name=\"surname\" value='"+ns[1]+"'>");
            } else {
                $(this).html("<input placeholder='name' class=\"update-db\" data-name=\"name\" value = ''><input placeholder='surname' class=\"update-db\" data-name=\"surname\" value=''>");
            }
        } else if ($(this).data("field")=='state') {
			$(this).html("<select class=\"state update-db\" data-name=\"state\"></select>");

			$.each(states, function(key, state) {
				$('.state')
				.append(
					$("<option></option>")
					.attr("value", state.id)
					.prop("selected", currentStateParent === state.name)
					.text(state.name)
				);
			});
		} else {
            $(this).html("<input placeholder='"+$(this).data("field")+"'  class=\"update-db\" data-name='"+$(this).data("field")+"' value = '"+currentValue+"'>");
        }
    });

    /** changing button to "Save" */
    $(this).text("Save info").removeClass("edit-parent").addClass("save-parent");
});

$('.parent-info').on('click', '.save-parent', function(e) {
	var $tab = $(this).closest('.parent-info');
	var parent_id = $(this).data('parent_id');
    var to_db = {};
    to_db.CSRF_token = '<?=$CSRF_token;?>';
    $.each($('.update-db', $tab), function() {
        to_db[$(this).data('name')] = $(this).val();
    });

    $.ajax({
        url: '?controller=parents&action=edit&parent_id='+encodeURIComponent(parent_id),
        data: to_db,
        async: true,
        cache: false,
        type: 'post',
        dataType: 'json',
        success: function(response, status, xhr) {
			bootbox.alert("No changes have been made", function() {
					location.href = location.href;
			});
            if (response.success) {
				location.href = location.href;
            }
        },
        error: function(xhr, err, descr) {}
    });
});
</script>
