import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../../services/dialog-service/dialog-service.service';
import { CategoryService } from '../../../services/api-services/category-service/category.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-new-category',
  templateUrl: './new-category.component.html',
  styleUrl: './new-category.component.css'
})
export class NewCategoryComponent {

  isSpinning: boolean = false;
  categoryForm: FormGroup;

  constructor(
    private dialogService: DialogService,
    private categoryService: CategoryService,
    private fb: FormBuilder,
    private router: Router
  ){
    this.categoryForm = this.fb.group({
      name: ["", [Validators.required]],
      description: ["", [Validators.required]]
    })
  }


  newCategory(){
    if (this.categoryForm.valid) {
      //this.dialogService.openSuccessDialog("OK", this.categoryForm.value.name + " " + this.categoryForm.value.description);
      this.isSpinning = true;
      this.categoryService.newCategory(this.categoryForm.value).subscribe({
        next: (response) => {
          //console.log(response);
          this.dialogService.openSuccessDialog("Operazione completata con successo!", "Nuova categoria aggiunta correttamente!")
          this.router.navigateByUrl("/admin/dashboard");
        },
        error: (error) => {
          //in caso di errore 401 il token-interceptor riporta alla pagina di login con mesaggio di errore
          if (error.status != 401) {
            this.dialogService.openErrorDialog("Errore", error.error);
          }
        }
      })
      this.isSpinning = false;
    }
    else{
      this.dialogService.openErrorDialog("Errore", "Compila correttamente i campi!");
    }
  }


}
