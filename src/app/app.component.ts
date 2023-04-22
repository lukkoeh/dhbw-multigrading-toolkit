import {Component} from '@angular/core';
import {read, utils} from "xlsx";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title :string = 'dhbw-multigrading-toolkit';
  datastart : number = 10;
  csvData : string = ""
  async handleFile(event: Event){
    const file:File = (event.currentTarget as HTMLInputElement).files![0];
    const data = await file.arrayBuffer();
    const workbook = read(data, /*{sheetRows: 7}*/);
    const workbookmeta = read(data, {sheetRows: 7})
    var jsonObject : any = utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]], {
      header: 1,
      raw: false,
    });
    let dataarray = jsonObject.slice(this.datastart-1)
    let metaarray = jsonObject.splice(0, 9)

    /*----RENDER METADATA TABLE----*/
    this.csvData = utils.sheet_to_html(workbookmeta.Sheets[workbookmeta.SheetNames[0]], {})

    /*----RENDER DATA TABLE----*/
    // headers
    this.csvData += "<table><tr>";
    for (let colIndex = 0; colIndex <=4; colIndex++) {
      this.csvData += "<th>";
      this.csvData += dataarray[0][colIndex];
      this.csvData += "</th>";
    }
    this.csvData += "</tr>";

    // actual data
    for (let rowIndex : number = 1; rowIndex<dataarray.length; rowIndex++) {
      // Excel import results in "undefined" cells being displayed
      if(dataarray[rowIndex][0] === undefined){
        break;
      }

      this.csvData += "<tr>";
      for (let colIndex = 0; colIndex <=4 ; colIndex++) {
        this.csvData += "<td>";
        this.csvData += dataarray[rowIndex][colIndex];
        this.csvData += "</td>";
      }
      this.csvData += "</tr>";
    }
    this.csvData += "</table>";
  }
}


