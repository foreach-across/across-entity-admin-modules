//
//  general UI elements behavior get attached here
//

// necessary so fancybox can attach itself to this version!
import $ from "jquery";
import "jquery.fancybox";
import logger from "logger";

// $.fancybox available here
$( () => {
  logger.log( "fancybox got loaded" );
} );

export default "elements loaded";
