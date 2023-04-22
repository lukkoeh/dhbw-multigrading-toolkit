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
    this.csvData = utils.sheet_to_html(workbookmeta.Sheets[workbookmeta.SheetNames[0]], {})
    this.csvData += "<table>"
    for (let i : number = 0; i<dataarray.length; i++) {
        if (i === 0) {
          this.csvData += "<th>"
          this.csvData += dataarray[i][0]
          this.csvData += dataarray[i][1]
          this.csvData += dataarray[i][2]
          this.csvData += dataarray[i][3]
          this.csvData += "</th>"
        }
    }
    this.csvData += "</table>"
  }
}


