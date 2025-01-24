export default {
    /**
     * Generates a random string for testing variable string values
     */
    randomString: function( length ) {
        return Math.round( (Math.pow( 36, length + 1 ) - Math.random() * Math.pow( 36, length )) ).toString( 36 ).slice( 1 );
    }
}
