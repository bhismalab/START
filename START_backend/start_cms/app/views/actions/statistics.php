<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

view::appendJs(SITE.CMS_DIR.JS_DIR.'highcharts/highcharts.js');

?>


<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_statistics_dashboard');?>
	</h1>
</section>


<section class="content">
	<div class="row">
		<div class="col-lg-4 col-xs-6">
			<div class="box box-warning">
				<div class="box-body">
					<div id="divChildrenGenderPieChart" style="margin: 0 0 37px 0;"></div>

					<table class="table">
						<tr><td>Children count</td><td><?=$children_count;?></td></tr>
						<tr><td>Male children</td><td><?=$children_M;?></td></tr>
						<tr><td>Female children</td><td><?=$children_F;?></td></tr>
					</table>

					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divChildrenGenderPieChart', {
        chart: {
            type: 'pie'
        },
        credits: false,
        title: {
            text: 'Children gender proportions'
        },
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		series: [{
			name: 'Children count',
			colorByPoint: true,
            size: 180,
			data: [{
				name: 'Male',
				y: <?=$children_M;?>,
			}, {
				name: 'Female',
				y: <?=$children_F;?>,
			}]
		}]
    });
});
// ]]>
					</script>
				</div>
			</div>
		</div>

		<div class="col-lg-4 col-xs-6">
			<div class="box box-info">
				<div class="box-body">
					<div id="divChildrenAgesPieChart" style="margin: 0 0 37px 0;"></div>
                    <table class="table">
                        <tr><td><3 year</td><td><?php if(isset($ages['<3'])) echo $ages['<3'];?></td></tr>
                        <tr><td>3-4 year</td><td><?php if(isset($ages['3-4'])) echo $ages['3-4'];?></td></tr>
                        <tr><td>>4 year</td><td> <?php if(isset($ages['>4'])) echo $ages['>4'];?></td></tr>
                    </table>
					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divChildrenAgesPieChart', {
        chart: {
            type: 'pie'
        },
        credits: false,
        title: {
            text: 'Children distribution by age ranges'
        },
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		series: [{
			name: 'Children count',
			colorByPoint: true,
			size: 180,
			data: [{
				name: '<3 y',
				y: <?php if(isset($ages['<3'])) {echo $ages['<3'];} else{ echo 0;}?>,
			}, {
				name: '3-4 y',
				y: <?php if(isset($ages['3-4'])){ echo $ages['3-4'];} else { echo 0;} ?>,
			}, {
				name: '>4 y',
				y: <?php if(isset($ages['>4'])) {echo $ages['>4'];} else{ echo 0;  }  ?>,
			}]
		}]
    });
});
// ]]>
					</script>
				</div>
			</div>
		</div>

		<div class="col-lg-4 col-xs-6">
			<div class="box box-warning">
				<div class="box-body">
					<div id="divSurveyStatusesPieChart"></div>

					<table class="table">
						<tr><td>Total surveys count</td><td><?=$surveys;?></td></tr>
						<tr><td>New suveys</td><td><?=$surveys_new;?></td></tr>
						<tr><td>Active suveys</td><td><?=$surveys_active;?></td></tr>
						<tr><td>Closed suveys</td><td><?=$surveys_closed;?></td></tr>
					</table>

					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divSurveyStatusesPieChart', {
        chart: {
            type: 'pie'
        },
        credits: false,
        title: {
            text: 'Survey statuses'
        },
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		series: [{
			name: 'Survey statuses',
			colorByPoint: true,
            size: 180,
			data: [{
				name: 'Active',
				y: <?=$surveys_active;?>,
			}, {
				name: 'Closed',
				y: <?=$surveys_closed;?>,
			}]
		}]
    });
});
// ]]>
					</script>
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-lg-3 col-xs-3">
			<div class="box box-success">
				<div class="box-body">
					<div id="divChildrenHandsPieChart" style="/*margin: 0 0 37px 0;*/"></div>

                    <table class="table">
                        <tr><td>Right</td><td><?=$hand_dominance['right'];?></td></tr>
                        <tr><td>Left</td><td><?=$hand_dominance['left'];?></td></tr>
                        <tr><td>Ambidextrous</td><td><?=$hand_dominance['ambidexter'];?></td></tr>
                        <tr><td>Undefined</td><td><?=$hand_dominance['undefined'];?></td></tr>
                    </table>

					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divChildrenHandsPieChart', {
        chart: {
            type: 'pie'
        },
        credits: false,
        title: {
            text: 'Preferable Hand'
        },
		tooltip: {
			pointFormat: '{series.name}: <b>{point.y} ({point.percentage:.1f}%)</b>'
		},
		series: [{
			name: 'Children count',
			colorByPoint: true,
			size: 180,
			data: [{
				name: 'Right',
				y: <?=$hand_dominance['right'];?>,
			}, {
				name: 'Left',
				y: <?=$hand_dominance['left'];?>,
			}, {
				name: 'Ambidexter',
				y: <?=$hand_dominance['ambidexter'];?>,
			}, {
				name: 'Undefined',
				y: <?=$hand_dominance['undefined'];?>,
			}]
		}]
    });
});
// ]]>
					</script>
				</div>
			</div>
		</div>

		<div class="col-lg-9 col-xs-9">
			<div class="box box-info">
				<div class="box-body">
					<!-- <pre><?=var_export($states, 1);?></pre> -->

					<div id="divChildrenStatesBarChart" style="height: 548px;"></div>

					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divChildrenStatesBarChart', {
		chart: {
			type: 'column'
		},
        credits: false,
		title: {
			text: 'Registered children amount per states'
		},
		xAxis: {
			type: 'category',
			labels: {
				rotation: -45,
				style: {
					fontSize: '13px',
					fontFamily: 'Verdana, sans-serif'
				}
			}
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Children count'
			}
		},
		legend: {
			enabled: false
		},
		/*tooltip: {
			pointFormat: 'Children count: <b>{point.y:.1f}</b>'
		},*/
		series: [{
			name: 'Children count',
			data: [
				<?php
				foreach ($states as $state) {
				?>['<?=utils::safeJsEcho($state['state'], 1);?>', <?=$state['children_count'];?>],<?php
				}
				?>
			],
			dataLabels: {
				enabled: true,
				rotation: -90,
				color: '#FFFFFF',
				align: 'right',
				//format: '{point.y:.1f}', // one decimal
				y: 10, // 10 pixels down from the top
				style: {
					fontSize: '13px',
					fontFamily: 'Verdana, sans-serif'
				}
			}
		}]
    });
});
// ]]>
					</script>

				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col-lg-12 col-xs-12">
			<div class="box box-success">
				<div class="box-body">

					<div id="divChildrenLangsColChart"></div>

					<!-- <pre><?=var_export(CMS::$db->queries, 1);?></pre> -->

					<script>
// <![CDATA[
$(function() {
    var myChart = Highcharts.chart('divChildrenLangsColChart', {
		chart: {
			type: 'column'
		},
        credits: false,
		title: {
			text: 'Spoken languages'
		},
		xAxis: {
			type: 'category',
			labels: {
				rotation: -45,
				style: {
					fontSize: '13px',
					fontFamily: 'Verdana, sans-serif'
				}
			}
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Speakers count'
			}
		},
		legend: {
			enabled: false
		},
		/*tooltip: {
			pointFormat: 'Speakers count: <b>{point.y:.1f}</b>'
		},*/
		series: [{
			name: 'Speakers count',
			data: [
				<?php
				foreach ($langs as $l) {
				?>['<?=utils::safeJsEcho($l['lang'], 1);?>', <?=$l['p_count'];?>],<?php
				}
				?>
			],
			dataLabels: {
				enabled: true,
				rotation: -90,
				color: '#FFFFFF',
				align: 'right',
				//format: '{point.y:.1f}', // one decimal
				y: 10, // 10 pixels down from the top
				style: {
					fontSize: '13px',
					fontFamily: 'Verdana, sans-serif'
				}
			}
		}]
    });
});
// ]]>
					</script>

				</div>
			</div>
		</div>
	</div>

	<!-- /.info boxes -->
</section>
<!-- /.content -->
