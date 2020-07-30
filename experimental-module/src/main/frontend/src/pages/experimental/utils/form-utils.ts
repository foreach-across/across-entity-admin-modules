export function getFormData($form: any) {
  // const data = new URLSearchParams();
  // var form = $form.serializeArray();
  // if ( form.length ) {
  //     for ( var i = 0; i < form.length; i++ ) {
  //         var key = form[i].name, value = form[i].value;
  //         if ( key.substr( key.length - 2, 2 ) === "[]" ) {
  //             key = key.substr( 0, key.length - 2 );
  //         }
  //
  //         if ( data.get( key ) != null ) {
  //             let valueToAppend: string | []  | null = data.get( key );
  //             if ( valueToAppend.push !== undefined ) {
  //                 valueToAppend!.push( value );
  //             }
  //             else {
  //                 valueToAppend! = [valueToAppend, value];
  //             }
  //             data.set( key, valueToAppend! );
  //         }
  //         else {
  //             data.append( key, value );
  //         }
  //     }
  //
  //     return data;
  // }
  return $form.serialize() || {};
}
