/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.bootstrapui.elements;

import lombok.NonNull;

/**
 * Represents a FontAwesome icon.
 *
 * @author Arne Vandamme
 */
public class FaIcon extends IconViewElement
{
	public interface WebApp
	{
		String ADJUST = "fa-adjust";
		String ANCHOR = "fa-anchor";
		String ARCHIVE = "fa-archive";
		String AREA_CHART = "fa-area-chart";
		String ARROWS = "fa-arrows";
		String ARROWS_H = "fa-arrows-h";
		String ARROWS_V = "fa-arrows-v";
		String ASTERISK = "fa-asterisk";
		String AT = "fa-at";
		String AUTOMOBILE = "fa-automobile";
		String BAN = "fa-ban";
		String BANK = "fa-bank";
		String BAR_CHART = "fa-bar-chart";
		String BAR_CHART_O = "fa-bar-chart-o";
		String BARCODE = "fa-barcode";
		String BARS = "fa-bars";
		String BED = "fa-bed";
		String BEER = "fa-beer";
		String BELL = "fa-bell";
		String BELL_O = "fa-bell-o";
		String BELL_SLASH = "fa-bell-slash";
		String BELL_SLASH_O = "fa-bell-slash-o";
		String BICYCLE = "fa-bicycle";
		String BINOCULARS = "fa-binoculars";
		String BIRTHDAY_CAKE = "fa-birthday-cake";
		String BOLT = "fa-bolt";
		String BOMB = "fa-bomb";
		String BOOK = "fa-book";
		String BOOKMARK = "fa-bookmark";
		String BOOKMARK_O = "fa-bookmark-o";
		String BRIEFCASE = "fa-briefcase";
		String BUG = "fa-bug";
		String BUILDING = "fa-building";
		String BUILDING_O = "fa-building-o";
		String BULLHORN = "fa-bullhorn";
		String BULLSEYE = "fa-bullseye";
		String BUS = "fa-bus";
		String CAB = "fa-cab";
		String CALCULATOR = "fa-calculator";
		String CALENDAR = "fa-calendar";
		String CALENDAR_O = "fa-calendar-o";
		String CAMERA = "fa-camera";
		String CAMERA_RETRO = "fa-camera-retro";
		String CAR = "fa-car";
		String CARET_SQUARE_O_DOWN = "fa-caret-square-o-down";
		String CARET_SQUARE_O_LEFT = "fa-caret-square-o-left";
		String CARET_SQUARE_O_RIGHT = "fa-caret-square-o-right";
		String CARET_SQUARE_O_UP = "fa-caret-square-o-up";
		String CART_ARROW_DOWN = "fa-cart-arrow-down";
		String CART_PLUS = "fa-cart-plus";
		String CC = "fa-cc";
		String CERTIFICATE = "fa-certificate";
		String CHECK = "fa-check";
		String CHECK_CIRCLE = "fa-check-circle";
		String CHECK_CIRCLE_O = "fa-check-circle-o";
		String CHECK_SQUARE = "fa-check-square";
		String CHECK_SQUARE_O = "fa-check-square-o";
		String CHILD = "fa-child";
		String CIRCLE = "fa-circle";
		String CIRCLE_O = "fa-circle-o";
		String CIRCLE_O_NOTCH = "fa-circle-o-notch";
		String CIRCLE_THIN = "fa-circle-thin";
		String CLOCK_O = "fa-clock-o";
		String CLOSE = "fa-close";
		String CLOUD = "fa-cloud";
		String CLOUD_DOWNLOAD = "fa-cloud-download";
		String CLOUD_UPLOAD = "fa-cloud-upload";
		String CODE = "fa-code";
		String CODE_FORK = "fa-code-fork";
		String COFFEE = "fa-coffee";
		String COG = "fa-cog";
		String COGS = "fa-cogs";
		String COMMENT = "fa-comment";
		String COMMENT_O = "fa-comment-o";
		String COMMENTS = "fa-comments";
		String COMMENTS_O = "fa-comments-o";
		String COMPASS = "fa-compass";
		String COPYRIGHT = "fa-copyright";
		String CREDIT_CARD = "fa-credit-card";
		String CROP = "fa-crop";
		String CROSSHAIRS = "fa-crosshairs";
		String CUBE = "fa-cube";
		String CUBES = "fa-cubes";
		String CUTLERY = "fa-cutlery";
		String DASHBOARD = "fa-dashboard";
		String DATABASE = "fa-database";
		String DESKTOP = "fa-desktop";
		String DIAMOND = "fa-diamond";
		String DOT_CIRCLE_O = "fa-dot-circle-o";
		String DOWNLOAD = "fa-download";
		String EDIT = "fa-edit";
		String ELLIPSIS_H = "fa-ellipsis-h";
		String ELLIPSIS_V = "fa-ellipsis-v";
		String ENVELOPE = "fa-envelope";
		String ENVELOPE_O = "fa-envelope-o";
		String ENVELOPE_SQUARE = "fa-envelope-square";
		String ERASER = "fa-eraser";
		String EXCHANGE = "fa-exchange";
		String EXCLAMATION = "fa-exclamation";
		String EXCLAMATION_CIRCLE = "fa-exclamation-circle";
		String EXCLAMATION_TRIANGLE = "fa-exclamation-triangle";
		String EXTERNAL_LINK = "fa-external-link";
		String EXTERNAL_LINK_SQUARE = "fa-external-link-square";
		String EYE = "fa-eye";
		String EYE_SLASH = "fa-eye-slash";
		String EYEDROPPER = "fa-eyedropper";
		String FAX = "fa-fax";
		String FEMALE = "fa-female";
		String FIGHTER_JET = "fa-fighter-jet";
		String FILE_ARCHIVE_O = "fa-file-archive-o";
		String FILE_AUDIO_O = "fa-file-audio-o";
		String FILE_CODE_O = "fa-file-code-o";
		String FILE_EXCEL_O = "fa-file-excel-o";
		String FILE_IMAGE_O = "fa-file-image-o";
		String FILE_MOVIE_O = "fa-file-movie-o";
		String FILE_PDF_O = "fa-file-pdf-o";
		String FILE_PHOTO_O = "fa-file-photo-o";
		String FILE_PICTURE_O = "fa-file-picture-o";
		String FILE_POWERPOINT_O = "fa-file-powerpoint-o";
		String FILE_SOUND_O = "fa-file-sound-o";
		String FILE_VIDEO_O = "fa-file-video-o";
		String FILE_WORD_O = "fa-file-word-o";
		String FILE_ZIP_O = "fa-file-zip-o";
		String FILM = "fa-film";
		String FILTER = "fa-filter";
		String FIRE = "fa-fire";
		String FIRE_EXTINGUISHER = "fa-fire-extinguisher";
		String FLAG = "fa-flag";
		String FLAG_CHECKERED = "fa-flag-checkered";
		String FLAG_O = "fa-flag-o";
		String FLASH = "fa-flash";
		String FLASK = "fa-flask";
		String FOLDER = "fa-folder";
		String FOLDER_O = "fa-folder-o";
		String FOLDER_OPEN = "fa-folder-open";
		String FOLDER_OPEN_O = "fa-folder-open-o";
		String FROWN_O = "fa-frown-o";
		String FUTBOL_O = "fa-futbol-o";
		String GAMEPAD = "fa-gamepad";
		String GAVEL = "fa-gavel";
		String GEAR = "fa-gear";
		String GEARS = "fa-gears";
		String GENDERLESS = "fa-genderless";
		String GIFT = "fa-gift";
		String GLASS = "fa-glass";
		String GLOBE = "fa-globe";
		String GRADUATION_CAP = "fa-graduation-cap";
		String GROUP = "fa-group";
		String HDD_O = "fa-hdd-o";
		String HEADPHONES = "fa-headphones";
		String HEART = "fa-heart";
		String HEART_O = "fa-heart-o";
		String HEARTBEAT = "fa-heartbeat";
		String HISTORY = "fa-history";
		String HOME = "fa-home";
		String HOTEL = "fa-hotel";
		String IMAGE = "fa-image";
		String INBOX = "fa-inbox";
		String INFO = "fa-info";
		String INFO_CIRCLE = "fa-info-circle";
		String INSTITUTION = "fa-institution";
		String KEY = "fa-key";
		String KEYBOARD_O = "fa-keyboard-o";
		String LANGUAGE = "fa-language";
		String LAPTOP = "fa-laptop";
		String LEAF = "fa-leaf";
		String LEGAL = "fa-legal";
		String LEMON_O = "fa-lemon-o";
		String LEVEL_DOWN = "fa-level-down";
		String LEVEL_UP = "fa-level-up";
		String LIFE_BOUY = "fa-life-bouy";
		String LIFE_BUOY = "fa-life-buoy";
		String LIFE_RING = "fa-life-ring";
		String LIFE_SAVER = "fa-life-saver";
		String LIGHTBULB_O = "fa-lightbulb-o";
		String LINE_CHART = "fa-line-chart";
		String LOCATION_ARROW = "fa-location-arrow";
		String LOCK = "fa-lock";
		String MAGIC = "fa-magic";
		String MAGNET = "fa-magnet";
		String MAIL_FORWARD = "fa-mail-forward";
		String MAIL_REPLY = "fa-mail-reply";
		String MAIL_REPLY_ALL = "fa-mail-reply-all";
		String MALE = "fa-male";
		String MAP_MARKER = "fa-map-marker";
		String MEH_O = "fa-meh-o";
		String MICROPHONE = "fa-microphone";
		String MICROPHONE_SLASH = "fa-microphone-slash";
		String MINUS = "fa-minus";
		String MINUS_CIRCLE = "fa-minus-circle";
		String MINUS_SQUARE = "fa-minus-square";
		String MINUS_SQUARE_O = "fa-minus-square-o";
		String MOBILE = "fa-mobile";
		String MOBILE_PHONE = "fa-mobile-phone";
		String MONEY = "fa-money";
		String MOON_O = "fa-moon-o";
		String MORTAR_BOARD = "fa-mortar-board";
		String MOTORCYCLE = "fa-motorcycle";
		String MUSIC = "fa-music";
		String NAVICON = "fa-navicon";
		String NEWSPAPER_O = "fa-newspaper-o";
		String PAINT_BRUSH = "fa-paint-brush";
		String PAPER_PLANE = "fa-paper-plane";
		String PAPER_PLANE_O = "fa-paper-plane-o";
		String PAW = "fa-paw";
		String PENCIL = "fa-pencil";
		String PENCIL_SQUARE = "fa-pencil-square";
		String PENCIL_SQUARE_O = "fa-pencil-square-o";
		String PHONE = "fa-phone";
		String PHONE_SQUARE = "fa-phone-square";
		String PHOTO = "fa-photo";
		String PICTURE_O = "fa-picture-o";
		String PIE_CHART = "fa-pie-chart";
		String PLANE = "fa-plane";
		String PLUG = "fa-plug";
		String PLUS = "fa-plus";
		String PLUS_CIRCLE = "fa-plus-circle";
		String PLUS_SQUARE = "fa-plus-square";
		String PLUS_SQUARE_O = "fa-plus-square-o";
		String POWER_OFF = "fa-power-off";
		String PRINT = "fa-print";
		String PUZZLE_PIECE = "fa-puzzle-piece";
		String QRCODE = "fa-qrcode";
		String QUESTION = "fa-question";
		String QUESTION_CIRCLE = "fa-question-circle";
		String QUOTE_LEFT = "fa-quote-left";
		String QUOTE_RIGHT = "fa-quote-right";
		String RANDOM = "fa-random";
		String RECYCLE = "fa-recycle";
		String REFRESH = "fa-refresh";
		String REMOVE = "fa-remove";
		String REORDER = "fa-reorder";
		String REPLY = "fa-reply";
		String REPLY_ALL = "fa-reply-all";
		String RETWEET = "fa-retweet";
		String ROAD = "fa-road";
		String ROCKET = "fa-rocket";
		String RSS = "fa-rss";
		String RSS_SQUARE = "fa-rss-square";
		String SEARCH = "fa-search";
		String SEARCH_MINUS = "fa-search-minus";
		String SEARCH_PLUS = "fa-search-plus";
		String SEND = "fa-send";
		String SEND_O = "fa-send-o";
		String SERVER = "fa-server";
		String SHARE = "fa-share";
		String SHARE_ALT = "fa-share-alt";
		String SHARE_ALT_SQUARE = "fa-share-alt-square";
		String SHARE_SQUARE = "fa-share-square";
		String SHARE_SQUARE_O = "fa-share-square-o";
		String SHIELD = "fa-shield";
		String SHIP = "fa-ship";
		String SHOPPING_CART = "fa-shopping-cart";
		String SIGN_IN = "fa-sign-in";
		String SIGN_OUT = "fa-sign-out";
		String SIGNAL = "fa-signal";
		String SITEMAP = "fa-sitemap";
		String SLIDERS = "fa-sliders";
		String SMILE_O = "fa-smile-o";
		String SOCCER_BALL_O = "fa-soccer-ball-o";
		String SORT = "fa-sort";
		String SORT_ALPHA_ASC = "fa-sort-alpha-asc";
		String SORT_ALPHA_DESC = "fa-sort-alpha-desc";
		String SORT_AMOUNT_ASC = "fa-sort-amount-asc";
		String SORT_AMOUNT_DESC = "fa-sort-amount-desc";
		String SORT_ASC = "fa-sort-asc";
		String SORT_DESC = "fa-sort-desc";
		String SORT_DOWN = "fa-sort-down";
		String SORT_NUMERIC_ASC = "fa-sort-numeric-asc";
		String SORT_NUMERIC_DESC = "fa-sort-numeric-desc";
		String SORT_UP = "fa-sort-up";
		String SPACE_SHUTTLE = "fa-space-shuttle";
		String SPINNER = "fa-spinner";
		String SPOON = "fa-spoon";
		String SQUARE = "fa-square";
		String SQUARE_O = "fa-square-o";
		String STAR = "fa-star";
		String STAR_HALF = "fa-star-half";
		String STAR_HALF_EMPTY = "fa-star-half-empty";
		String STAR_HALF_FULL = "fa-star-half-full";
		String STAR_HALF_O = "fa-star-half-o";
		String STAR_O = "fa-star-o";
		String STREET_VIEW = "fa-street-view";
		String SUITCASE = "fa-suitcase";
		String SUN_O = "fa-sun-o";
		String SUPPORT = "fa-support";
		String TABLET = "fa-tablet";
		String TACHOMETER = "fa-tachometer";
		String TAG = "fa-tag";
		String TAGS = "fa-tags";
		String TASKS = "fa-tasks";
		String TAXI = "fa-taxi";
		String TERMINAL = "fa-terminal";
		String THUMB_TACK = "fa-thumb-tack";
		String THUMBS_DOWN = "fa-thumbs-down";
		String THUMBS_O_DOWN = "fa-thumbs-o-down";
		String THUMBS_O_UP = "fa-thumbs-o-up";
		String THUMBS_UP = "fa-thumbs-up";
		String TICKET = "fa-ticket";
		String TIMES = "fa-times";
		String TIMES_CIRCLE = "fa-times-circle";
		String TIMES_CIRCLE_O = "fa-times-circle-o";
		String TINT = "fa-tint";
		String TOGGLE_DOWN = "fa-toggle-down";
		String TOGGLE_LEFT = "fa-toggle-left";
		String TOGGLE_OFF = "fa-toggle-off";
		String TOGGLE_ON = "fa-toggle-on";
		String TOGGLE_RIGHT = "fa-toggle-right";
		String TOGGLE_UP = "fa-toggle-up";
		String TRASH = "fa-trash";
		String TRASH_O = "fa-trash-o";
		String TREE = "fa-tree";
		String TROPHY = "fa-trophy";
		String TRUCK = "fa-truck";
		String TTY = "fa-tty";
		String UMBRELLA = "fa-umbrella";
		String UNIVERSITY = "fa-university";
		String UNLOCK = "fa-unlock";
		String UNLOCK_ALT = "fa-unlock-alt";
		String UNSORTED = "fa-unsorted";
		String UPLOAD = "fa-upload";
		String USER = "fa-user";
		String USER_PLUS = "fa-user-plus";
		String USER_SECRET = "fa-user-secret";
		String USER_TIMES = "fa-user-times";
		String USERS = "fa-users";
		String VIDEO_CAMERA = "fa-video-camera";
		String VOLUME_DOWN = "fa-volume-down";
		String VOLUME_OFF = "fa-volume-off";
		String VOLUME_UP = "fa-volume-up";
		String WARNING = "fa-warning";
		String WHEELCHAIR = "fa-wheelchair";
		String WIFI = "fa-wifi";
		String WRENCH = "fa-wrench";
	}

	public interface Transport
	{
		String AMBULANCE = "fa-ambulance";
		String AUTOMOBILE = "fa-automobile";
		String BICYCLE = "fa-bicycle";
		String BUS = "fa-bus";
		String CAB = "fa-cab";
		String CAR = "fa-car";
		String FIGHTER_JET = "fa-fighter-jet";
		String MOTORCYCLE = "fa-motorcycle";
		String PLANE = "fa-plane";
		String ROCKET = "fa-rocket";
		String SHIP = "fa-ship";
		String SPACE_SHUTTLE = "fa-space-shuttle";
		String SUBWAY = "fa-subway";
		String TAXI = "fa-taxi";
		String TRAIN = "fa-train";
		String TRUCK = "fa-truck";
		String WHEELCHAIR = "fa-wheelchair";
	}

	public interface Gender
	{
		String CIRCLE_THIN = "fa-circle-thin";
		String GENDERLESS = "fa-genderless";
		String MARS = "fa-mars";
		String MARS_DOUBLE = "fa-mars-double";
		String MARS_STROKE = "fa-mars-stroke";
		String MARS_STROKE_H = "fa-mars-stroke-h";
		String MARS_STROKE_V = "fa-mars-stroke-v";
		String MERCURY = "fa-mercury";
		String NEUTER = "fa-neuter";
		String TRANSGENDER = "fa-transgender";
		String TRANSGENDER_ALT = "fa-transgender-alt";
		String VENUS = "fa-venus";
		String VENUS_DOUBLE = "fa-venus-double";
		String VENUS_MARS = "fa-venus-mars";
	}

	public interface FileType
	{
		String FILE = "fa-file";
		String FILE_ARCHIVE_O = "fa-file-archive-o";
		String FILE_AUDIO_O = "fa-file-audio-o";
		String FILE_CODE_O = "fa-file-code-o";
		String FILE_EXCEL_O = "fa-file-excel-o";
		String FILE_IMAGE_O = "fa-file-image-o";
		String FILE_MOVIE_O = "fa-file-movie-o";
		String FILE_O = "fa-file-o";
		String FILE_PDF_O = "fa-file-pdf-o";
		String FILE_PHOTO_O = "fa-file-photo-o";
		String FILE_PICTURE_O = "fa-file-picture-o";
		String FILE_POWERPOINT_O = "fa-file-powerpoint-o";
		String FILE_SOUND_O = "fa-file-sound-o";
		String FILE_TEXT = "fa-file-text";
		String FILE_TEXT_O = "fa-file-text-o";
		String FILE_VIDEO_O = "fa-file-video-o";
		String FILE_WORD_O = "fa-file-word-o";
		String FILE_ZIP_O = "fa-file-zip-o";
	}

	public interface Spin
	{
		String CIRCLE_O_NOTCH = "fa-circle-o-notch";
		String COG = "fa-cog";
		String GEAR = "fa-gear";
		String REFRESH = "fa-refresh";
		String SPINNER = "fa-spinner";
	}

	public interface Form
	{
		String CHECK_SQUARE = "fa-check-square";
		String CHECK_SQUARE_O = "fa-check-square-o";
		String CIRCLE = "fa-circle";
		String CIRCLE_O = "fa-circle-o";
		String DOT_CIRCLE_O = "fa-dot-circle-o";
		String MINUS_SQUARE = "fa-minus-square";
		String MINUS_SQUARE_O = "fa-minus-square-o";
		String PLUS_SQUARE = "fa-plus-square";
		String PLUS_SQUARE_O = "fa-plus-square-o";
		String SQUARE = "fa-square";
		String SQUARE_O = "fa-square-o";
	}

	public interface Payment
	{
		String CC_AMEX = "fa-cc-amex";
		String CC_DISCOVER = "fa-cc-discover";
		String CC_MASTERCARD = "fa-cc-mastercard";
		String CC_PAYPAL = "fa-cc-paypal";
		String CC_STRIPE = "fa-cc-stripe";
		String CC_VISA = "fa-cc-visa";
		String CREDIT_CARD = "fa-credit-card";
		String GOOGLE_WALLET = "fa-google-wallet";
		String PAYPAL = "fa-paypal";
	}

	public interface Chart
	{
		String AREA_CHART = "fa-area-chart";
		String BAR_CHART = "fa-bar-chart";
		String BAR_CHART_O = "fa-bar-chart-o";
		String LINE_CHART = "fa-line-chart";
		String PIE_CHART = "fa-pie-chart";
	}

	public interface Currency
	{
		String BITCOIN = "fa-bitcoin";
		String BTC = "fa-btc";
		String CNY = "fa-cny";
		String DOLLAR = "fa-dollar";
		String EUR = "fa-eur";
		String EURO = "fa-euro";
		String GBP = "fa-gbp";
		String ILS = "fa-ils";
		String INR = "fa-inr";
		String JPY = "fa-jpy";
		String KRW = "fa-krw";
		String MONEY = "fa-money";
		String RMB = "fa-rmb";
		String ROUBLE = "fa-rouble";
		String RUB = "fa-rub";
		String RUBLE = "fa-ruble";
		String RUPEE = "fa-rupee";
		String SHEKEL = "fa-shekel";
		String SHEQEL = "fa-sheqel";
		String TRY = "fa-try";
		String TURKISH_LIRA = "fa-turkish-lira";
		String USD = "fa-usd";
		String WON = "fa-won";
		String YEN = "fa-yen";
	}

	public interface TextEditor
	{
		String ALIGN_CENTER = "fa-align-center";
		String ALIGN_JUSTIFY = "fa-align-justify";
		String ALIGN_LEFT = "fa-align-left";
		String ALIGN_RIGHT = "fa-align-right";
		String BOLD = "fa-bold";
		String CHAIN = "fa-chain";
		String CHAIN_BROKEN = "fa-chain-broken";
		String CLIPBOARD = "fa-clipboard";
		String COLUMNS = "fa-columns";
		String COPY = "fa-copy";
		String CUT = "fa-cut";
		String DEDENT = "fa-dedent";
		String ERASER = "fa-eraser";
		String FILE = "fa-file";
		String FILE_O = "fa-file-o";
		String FILE_TEXT = "fa-file-text";
		String FILE_TEXT_O = "fa-file-text-o";
		String FILES_O = "fa-files-o";
		String FLOPPY_O = "fa-floppy-o";
		String FONT = "fa-font";
		String HEADER = "fa-header";
		String INDENT = "fa-indent";
		String ITALIC = "fa-italic";
		String LINK = "fa-link";
		String LIST = "fa-list";
		String LIST_ALT = "fa-list-alt";
		String LIST_OL = "fa-list-ol";
		String LIST_UL = "fa-list-ul";
		String OUTDENT = "fa-outdent";
		String PAPERCLIP = "fa-paperclip";
		String PARAGRAPH = "fa-paragraph";
		String PASTE = "fa-paste";
		String REPEAT = "fa-repeat";
		String ROTATE_LEFT = "fa-rotate-left";
		String ROTATE_RIGHT = "fa-rotate-right";
		String SAVE = "fa-save";
		String SCISSORS = "fa-scissors";
		String STRIKETHROUGH = "fa-strikethrough";
		String SUBSCRIPT = "fa-subscript";
		String SUPERSCRIPT = "fa-superscript";
		String TABLE = "fa-table";
		String TEXT_HEIGHT = "fa-text-height";
		String TEXT_WIDTH = "fa-text-width";
		String TH = "fa-th";
		String TH_LARGE = "fa-th-large";
		String TH_LIST = "fa-th-list";
		String UNDERLINE = "fa-underline";
		String UNDO = "fa-undo";
		String UNLINK = "fa-unlink";
	}

	public interface Directional
	{
		String ANGLE_DOUBLE_DOWN = "fa-angle-double-down";
		String ANGLE_DOUBLE_LEFT = "fa-angle-double-left";
		String ANGLE_DOUBLE_RIGHT = "fa-angle-double-right";
		String ANGLE_DOUBLE_UP = "fa-angle-double-up";
		String ANGLE_DOWN = "fa-angle-down";
		String ANGLE_LEFT = "fa-angle-left";
		String ANGLE_RIGHT = "fa-angle-right";
		String ANGLE_UP = "fa-angle-up";
		String ARROW_CIRCLE_DOWN = "fa-arrow-circle-down";
		String ARROW_CIRCLE_LEFT = "fa-arrow-circle-left";
		String ARROW_CIRCLE_O_DOWN = "fa-arrow-circle-o-down";
		String ARROW_CIRCLE_O_LEFT = "fa-arrow-circle-o-left";
		String ARROW_CIRCLE_O_RIGHT = "fa-arrow-circle-o-right";
		String ARROW_CIRCLE_O_UP = "fa-arrow-circle-o-up";
		String ARROW_CIRCLE_RIGHT = "fa-arrow-circle-right";
		String ARROW_CIRCLE_UP = "fa-arrow-circle-up";
		String ARROW_DOWN = "fa-arrow-down";
		String ARROW_LEFT = "fa-arrow-left";
		String ARROW_RIGHT = "fa-arrow-right";
		String ARROW_UP = "fa-arrow-up";
		String ARROWS = "fa-arrows";
		String ARROWS_ALT = "fa-arrows-alt";
		String ARROWS_H = "fa-arrows-h";
		String ARROWS_V = "fa-arrows-v";
		String CARET_DOWN = "fa-caret-down";
		String CARET_LEFT = "fa-caret-left";
		String CARET_RIGHT = "fa-caret-right";
		String CARET_SQUARE_O_DOWN = "fa-caret-square-o-down";
		String CARET_SQUARE_O_LEFT = "fa-caret-square-o-left";
		String CARET_SQUARE_O_RIGHT = "fa-caret-square-o-right";
		String CARET_SQUARE_O_UP = "fa-caret-square-o-up";
		String CARET_UP = "fa-caret-up";
		String CHEVRON_CIRCLE_DOWN = "fa-chevron-circle-down";
		String CHEVRON_CIRCLE_LEFT = "fa-chevron-circle-left";
		String CHEVRON_CIRCLE_RIGHT = "fa-chevron-circle-right";
		String CHEVRON_CIRCLE_UP = "fa-chevron-circle-up";
		String CHEVRON_DOWN = "fa-chevron-down";
		String CHEVRON_LEFT = "fa-chevron-left";
		String CHEVRON_RIGHT = "fa-chevron-right";
		String CHEVRON_UP = "fa-chevron-up";
		String HAND_O_DOWN = "fa-hand-o-down";
		String HAND_O_LEFT = "fa-hand-o-left";
		String HAND_O_RIGHT = "fa-hand-o-right";
		String HAND_O_UP = "fa-hand-o-up";
		String LONG_ARROW_DOWN = "fa-long-arrow-down";
		String LONG_ARROW_LEFT = "fa-long-arrow-left";
		String LONG_ARROW_RIGHT = "fa-long-arrow-right";
		String LONG_ARROW_UP = "fa-long-arrow-up";
		String TOGGLE_DOWN = "fa-toggle-down";
		String TOGGLE_LEFT = "fa-toggle-left";
		String TOGGLE_RIGHT = "fa-toggle-right";
		String TOGGLE_UP = "fa-toggle-up";
	}

	public interface VideoPlayer
	{
		String ARROWS_ALT = "fa-arrows-alt";
		String BACKWARD = "fa-backward";
		String COMPRESS = "fa-compress";
		String EJECT = "fa-eject";
		String EXPAND = "fa-expand";
		String FAST_BACKWARD = "fa-fast-backward";
		String FAST_FORWARD = "fa-fast-forward";
		String FORWARD = "fa-forward";
		String PAUSE = "fa-pause";
		String PLAY = "fa-play";
		String PLAY_CIRCLE = "fa-play-circle";
		String PLAY_CIRCLE_O = "fa-play-circle-o";
		String STEP_BACKWARD = "fa-step-backward";
		String STEP_FORWARD = "fa-step-forward";
		String STOP = "fa-stop";
		String YOUTUBE_PLAY = "fa-youtube-play";
	}

	public interface Brand
	{
		String ADN = "fa-adn";
		String ANDROID = "fa-android";
		String ANGELLIST = "fa-angellist";
		String APPLE = "fa-apple";
		String BEHANCE = "fa-behance";
		String BEHANCE_SQUARE = "fa-behance-square";
		String BITBUCKET = "fa-bitbucket";
		String BITBUCKET_SQUARE = "fa-bitbucket-square";
		String BITCOIN = "fa-bitcoin";
		String BTC = "fa-btc";
		String BUYSELLADS = "fa-buysellads";
		String CC_AMEX = "fa-cc-amex";
		String CC_DISCOVER = "fa-cc-discover";
		String CC_MASTERCARD = "fa-cc-mastercard";
		String CC_PAYPAL = "fa-cc-paypal";
		String CC_STRIPE = "fa-cc-stripe";
		String CC_VISA = "fa-cc-visa";
		String CODEPEN = "fa-codepen";
		String CONNECTDEVELOP = "fa-connectdevelop";
		String CSS3 = "fa-css3";
		String DASHCUBE = "fa-dashcube";
		String DELICIOUS = "fa-delicious";
		String DEVIANTART = "fa-deviantart";
		String DIGG = "fa-digg";
		String DRIBBBLE = "fa-dribbble";
		String DROPBOX = "fa-dropbox";
		String DRUPAL = "fa-drupal";
		String EMPIRE = "fa-empire";
		String FACEBOOK = "fa-facebook";
		String FACEBOOK_F = "fa-facebook-f";
		String FACEBOOK_OFFICIAL = "fa-facebook-official";
		String FACEBOOK_SQUARE = "fa-facebook-square";
		String FLICKR = "fa-flickr";
		String FORUMBEE = "fa-forumbee";
		String FOURSQUARE = "fa-foursquare";
		String GE = "fa-ge";
		String GIT = "fa-git";
		String GIT_SQUARE = "fa-git-square";
		String GITHUB = "fa-github";
		String GITHUB_ALT = "fa-github-alt";
		String GITHUB_SQUARE = "fa-github-square";
		String GITTIP = "fa-gittip";
		String GOOGLE = "fa-google";
		String GOOGLE_PLUS = "fa-google-plus";
		String GOOGLE_PLUS_SQUARE = "fa-google-plus-square";
		String GOOGLE_WALLET = "fa-google-wallet";
		String GRATIPAY = "fa-gratipay";
		String HACKER_NEWS = "fa-hacker-news";
		String HTML5 = "fa-html5";
		String INSTAGRAM = "fa-instagram";
		String IOXHOST = "fa-ioxhost";
		String JOOMLA = "fa-joomla";
		String JSFIDDLE = "fa-jsfiddle";
		String LASTFM = "fa-lastfm";
		String LASTFM_SQUARE = "fa-lastfm-square";
		String LEANPUB = "fa-leanpub";
		String LINKEDIN = "fa-linkedin";
		String LINKEDIN_SQUARE = "fa-linkedin-square";
		String LINUX = "fa-linux";
		String MAXCDN = "fa-maxcdn";
		String MEANPATH = "fa-meanpath";
		String MEDIUM = "fa-medium";
		String OPENID = "fa-openid";
		String PAGELINES = "fa-pagelines";
		String PAYPAL = "fa-paypal";
		String PIED_PIPER = "fa-pied-piper";
		String PIED_PIPER_ALT = "fa-pied-piper-alt";
		String PINTEREST = "fa-pinterest";
		String PINTEREST_P = "fa-pinterest-p";
		String PINTEREST_SQUARE = "fa-pinterest-square";
		String QQ = "fa-qq";
		String RA = "fa-ra";
		String REBEL = "fa-rebel";
		String REDDIT = "fa-reddit";
		String REDDIT_SQUARE = "fa-reddit-square";
		String RENREN = "fa-renren";
		String SELLSY = "fa-sellsy";
		String SHARE_ALT = "fa-share-alt";
		String SHARE_ALT_SQUARE = "fa-share-alt-square";
		String SHIRTSINBULK = "fa-shirtsinbulk";
		String SIMPLYBUILT = "fa-simplybuilt";
		String SKYATLAS = "fa-skyatlas";
		String SKYPE = "fa-skype";
		String SLACK = "fa-slack";
		String SLIDESHARE = "fa-slideshare";
		String SOUNDCLOUD = "fa-soundcloud";
		String SPOTIFY = "fa-spotify";
		String STACK_EXCHANGE = "fa-stack-exchange";
		String STACK_OVERFLOW = "fa-stack-overflow";
		String STEAM = "fa-steam";
		String STEAM_SQUARE = "fa-steam-square";
		String STUMBLEUPON = "fa-stumbleupon";
		String STUMBLEUPON_CIRCLE = "fa-stumbleupon-circle";
		String TENCENT_WEIBO = "fa-tencent-weibo";
		String TRELLO = "fa-trello";
		String TUMBLR = "fa-tumblr";
		String TUMBLR_SQUARE = "fa-tumblr-square";
		String TWITCH = "fa-twitch";
		String TWITTER = "fa-twitter";
		String TWITTER_SQUARE = "fa-twitter-square";
		String VIACOIN = "fa-viacoin";
		String VIMEO_SQUARE = "fa-vimeo-square";
		String VINE = "fa-vine";
		String VK = "fa-vk";
		String WECHAT = "fa-wechat";
		String WEIBO = "fa-weibo";
		String WEIXIN = "fa-weixin";
		String WHATSAPP = "fa-whatsapp";
		String WINDOWS = "fa-windows";
		String WORDPRESS = "fa-wordpress";
		String XING = "fa-xing";
		String XING_SQUARE = "fa-xing-square";
		String YAHOO = "fa-yahoo";
		String YELP = "fa-yelp";
		String YOUTUBE = "fa-youtube";
		String YOUTUBE_PLAY = "fa-youtube-play";
		String YOUTUBE_SQUARE = "fa-youtube-square";
	}

	public interface Medical
	{
		String AMBULANCE = "fa-ambulance";
		String H_SQUARE = "fa-h-square";
		String HEART = "fa-heart";
		String HEART_O = "fa-heart-o";
		String HEARTBEAT = "fa-heartbeat";
		String HOSPITAL_O = "fa-hospital-o";
		String MEDKIT = "fa-medkit";
		String PLUS_SQUARE = "fa-plus-square";
		String STETHOSCOPE = "fa-stethoscope";
		String USER_MD = "fa-user-md";
		String WHEELCHAIR = "fa-wheelchair";
	}

	private String glyph;

	public FaIcon() {
		setTagName( "i" );
	}

	public FaIcon( String glyph ) {
		setTagName( "i" );
		setGlyph( glyph );
	}

	public FaIcon setGlyph( @NonNull String glyph ) {
		this.glyph = glyph;
		setIconCss( "fa " + glyph );
		return this;
	}

	public String getGlyph() {
		return glyph;
	}
}
