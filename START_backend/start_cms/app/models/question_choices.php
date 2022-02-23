<?php

namespace app\models;

//if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class question_choices {
    // hardcoded for now - we may need to move them in a table
    public static $choices = [
        0 => [
            'english' => ['Yes', 'No'],
            'hindi' => ['हाँ', 'नही']
        ],
        1 => [
            'english' => ['Agree', 'Disagree'],
            'hindi' => ['सहमत हैं', 'असहमत हैं']
        ],
        2 => [
            'english' => ['Yes', 'No', 'Not sure'],
            'hindi' => ['हाँ', 'नही', 'पूरे विश्वास से नहीं कह सकते']
        ],
        3 => [
            'english' => ['Definitely agree', 'Slightly agree', 'Slightly disagree', 'Definitely disagree'],
            'hindi' => ['पूरी तरह से सहमत हैं', 'थोड़ासहमत हैं', 'थोड़ा असहमत हैं', 'पूरी तरह से असहमत हैं']
        ],
        4 => [
            'english' => ['Always', 'Sometimes', 'Rarely', 'Never'],
            'hindi' => ['हमेशा', 'अक्सर', 'कभी-कभी', 'कभी नहीं']
        ]
    ];

    public static function getChoicesList() {
        return self::$choices;
    }

    public static function getChoicesById($id) {
		return self::$choices[$id];
	}
}

?>