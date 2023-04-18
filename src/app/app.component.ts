import {Component, ViewChild} from '@angular/core';
import {read, readFile, utils} from "xlsx";
import * as electron from "electron";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'dhbw-multigrading-toolkit';
  csvData = 'kacke';
  async handleFile(event: Event){
    const file:File = (event.currentTarget as HTMLInputElement).files![0];
    const data = await file.arrayBuffer();
    const workbook = read(data);
    var jsonString = utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]]);
    this.csvData = JSON.stringify(jsonString);
  }
}


