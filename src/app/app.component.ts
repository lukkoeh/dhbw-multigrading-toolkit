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
  csvData : string = "<h1>bruh</h1>"
  async handleFile(event: Event){
    const file:File = (event.currentTarget as HTMLInputElement).files![0];
    const data = await file.arrayBuffer();
    const workbook = read(data, /*{sheetRows: 7}*/);
    var jsonObject : any = utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]], {
      header: 1,
      raw: false,
    });
    let dataarray = jsonObject.slice(this.datastart-1)
    let metaarray = jsonObject.splice(0, 9)
    this.csvData="<h1>blyat</h1>"
  }
}


