// Run using nodejs: nodejs print-10year-timetable

var fs = require('fs');
// file is included here:
eval(fs.readFileSync('PrayTimes.js') + '');

function zeroFill(number, width) {
	width -= number.toString().length;
	if ( width > 0 ) {
		return new Array( width + (/\./.test( number ) ? 2 : 1) ).join( '0' ) + number;
	}
	return number + ""; // always return a string
}

var date = new Date(2013, 6, 24);

// sample-00.txt
var tzo = '-5';
var adjustment = { };
var tunes = { };
var loc = [ 43, -80 ];

// sample-01.txt
// var tzo = '7';
// var adjustment = { fajr: 20, dhuhr: '2 min', maghrib: 1, isha: 18 };
// var tunes = { fajr: 2, sunrise: -2, asr: 2, maghrib: 2, isha: 2 };
// var loc = [ -6.1744444, 106.8294444 ];

// sample-02.txt
// var tzo = '-5';
// var adjustment = { fajr: 19, dhuhr: '5 min', maghrib: 2, isha: 15 };
// var tunes = { fajr: 2 };
// var loc = [ 43, -80 ];

prayTimes.adjust(adjustment);
prayTimes.tune(tunes);

for (i = 0; i < 365 * 10; i++) {
	var times = prayTimes.getTimes(date, loc, tzo, 0, '24h');

	console.log(zeroFill(i + 1, 4) + " | " + 
		times.fajr + " | " + 
		times.sunrise + " | " + 
		times.dhuhr + " | " + 
		times.asr + " | " + 
		times.maghrib + " | " + 
		times.isha);

	// Next day
	date.setTime(date.getTime() + 86400000);
}
