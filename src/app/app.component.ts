import {Component, ViewChild} from '@angular/core';
import {read, readFile, utils} from "xlsx";
import * as electron from "electron";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title :string = 'dhbw-multigrading-toolkit';
  csvData:string = 'kacke';
  async handleFile(event: Event){
    const file:File = (event.currentTarget as HTMLInputElement).files![0];
    const data = await file.arrayBuffer();
    const workbook = read(data, {sheetRows: 7});
    var jsonObject : any = utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]], {
      header: 1,
      raw: false,
    });
    this.csvData = JSON.stringify(jsonObject);
  }
}


