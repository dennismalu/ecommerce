import { Component } from '@angular/core';
import { CategoryService } from '../../../services/api-services/category-service/category.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductService } from '../../../services/api-services/product-service/product.service';
import { DialogService } from '../../../services/dialog-service/dialog-service.service';
import { Router } from '@angular/router';
import { ImagesService } from '../../../services/images-service/images.service';

@Component({
  selector: 'app-new-product',
  templateUrl: './new-product.component.html',
  styleUrl: './new-product.component.css'
})
export class NewProductComponent {

  isSpinning = false;
  categories: any;
  selectedImageFile: File | null;
  imagePreview: string | null;
  newProductForm: FormGroup;

  constructor(
    private categoryService: CategoryService,
    private fb: FormBuilder,
    private productService: ProductService,
    private dialogService: DialogService,
    private router: Router
  ){ 
    this.selectedImageFile = null; 
    // Imposto l'immagine di default
    this.imagePreview = null;
    this.newProductForm = this.fb.group({
      categoryId: ["", Validators.required],
      name: ["", [ 
          Validators.required, 
          Validators.maxLength(60)
          //il nome non può superare i 60 caratteri
        ]
      ],
      price: ["", [
          Validators.required,
          Validators.pattern(/^\d+(\.\d{1,2})?$/)
          // ^ --> inizio della stringa
          // \d+ --> una o più cifre (parte intera)
          // (\.\d{1,2})? --> parte decimale opzionale:
          //      \. --> punto decimale
          //      \d{1,2} --> una o due cifre decimali
          //      ? --> rende tutto il gruppo opzionale
          // $ - fine della stringa
        ]
      ],
      description: ["", Validators.required]
    }); 
  }

  ngOnInit(){
    this.setAllCategories();
  }

  removeImage(): void {
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  onFileSelected(event: any){
    const file = event.target.files[0];

    //per gestire l'anteprima dell'immagine
     if (file) {
      this.selectedImageFile = file;
      
      // Crea l'anteprima dell'immagine
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }

    //console.log("Imagefile: " + this.selectedImageFile)
    //console.log("imagePreview: " + this.imagePreview)
  }
  
  // Gestione errori nel caricamento dell'immagine
  onImageError(event: any) {
    console.error('Errore nel caricamento dell\'immagine:', event);
    // Se l'immagine di default non viene trovata --> fallback
    this.removeImage();
    this.dialogService.openErrorDialog("Errore", "Errore nel caricamento dell'immagine: immagine danneggiata! Seleziona un'altra immagine!");
  }



  setAllCategories(){
    this.categoryService.getAllCategories().subscribe((res) =>{
      //console.log(res);
      this.categories = res;
    })
  }

  addProduct(){
    if (this.newProductForm.valid) {
      this.isSpinning = true;

      // Se non c'è un file immagine selezionato invio image: null
      if (!this.selectedImageFile) {
        const productDTO = {
          name: this.newProductForm.get('name')?.value,
          description: this.newProductForm.get('description')?.value,
          price: this.newProductForm.get('price')?.value,
          image: null, //se non c'è un file selezionato invio null
          categoryId: this.newProductForm.get('categoryId')?.value
        };

        this.chiamataPost(productDTO);
      }

      // Se c'è un file immagine selezionato lo converto ad array di byte
      else {
        ImagesService.convertFileToByteArray(this.selectedImageFile).then(byteArray => {
          const productDTO = {
            name: this.newProductForm.get('name')?.value,
            description: this.newProductForm.get('description')?.value,
            price: this.newProductForm.get('price')?.value,
            image: byteArray, // Invia come array di byte
            categoryId: this.newProductForm.get('categoryId')?.value
          };

          console.log(productDTO);

          this.chiamataPost(productDTO);

        }).catch(error => {
          console.error('Errore nella conversione del file:', error);
          this.isSpinning = false;
          this.dialogService.openErrorDialog("Errore", "Seleziona un'altra immagine!");
        });
      } 
    }
    else {
      this.dialogService.openErrorDialog("Errore", "Compila correttamente tutti i campi!");
    }
  }

  chiamataPost(productDTO: any){
    console.log(productDTO);

    this.productService.newProduct(productDTO).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Nuovo prodotto aggiunto correttamente!");
        this.router.navigateByUrl("/admin/dashboard");
      },
      error: (error) => {
        console.log(error);
        if (error.status != 401) {
          this.dialogService.openErrorDialog("Errore", error.error);
        }
      }
    });
    this.isSpinning = false;
  }


}
