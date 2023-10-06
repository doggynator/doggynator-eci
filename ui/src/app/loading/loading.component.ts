import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from '../_services/storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css']
})
export class LoadingComponent implements OnInit {

  content : any;

  constructor(private userService : UserService, private storageService : StorageService, private router: Router) { }

  ngOnInit(): void {

    this.userService.getUStage(this.storageService.getUser().id).subscribe({
      next: data => {
        console.log(data);
        this.content = data;
        if(this.content.stage == 1){
          this.router.navigate(['/cards']).then(() => {
            window.location.reload();
          });
        }else if(this.content.stage == 2){
          this.router.navigate(['/recomenders']).then(() => {
            window.location.reload();
          });
        }else{
          this.router.navigate(['/settings']).then(() => {
            window.location.reload();
          });
        }


      },
      error: err => {
        if (err.error) {
          try {
            const res = JSON.parse(err.error);
          } catch {
          }
        }
      }
    });
  }

}
