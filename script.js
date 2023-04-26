// Laden Sie die SheetJS-Bibliothek
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.16.9/xlsx.full.min.js"></script>

// Lesen Sie eine Excel-Datei und geben Sie die Daten in der Konsole aus
var file = document.getElementById("input-file").files[0]; // Die hochgeladene Excel-Datei
var reader = new FileReader();

reader.onload = function(e) {
  var data = new Uint8Array(e.target.result);
  var workbook = XLSX.read(data, {type: 'array'});
  var sheet_name_list = workbook.SheetNames;
  sheet_name_list.forEach(function(y) {
    var worksheet = workbook.Sheets[y];
    var headers = {};
    var data = [];
    for(z in worksheet) {
      if(z[0] === '!') continue;
      //parse out the column, row, and value
      var col = z.substring(0,1);
      var row = parseInt(z.substring(1));
      var value = worksheet[z].v;

      //store header names
      if(row == 1) {
        headers[col] = value;
        continue;
      }

      if(!data[row]) data[row]={};
      data[row][headers[col]] = value;
    }
    //drop those first two rows which are empty
    data.shift();
    data.shift();
    console.log(data);
  });
};

reader.readAsArrayBuffer(file);
