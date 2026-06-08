import { Component } from '@angular/core';
import { ProductService } from '../../../services/api-services/product-service/product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from '../../../services/api-services/category-service/category.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DialogService } from '../../../services/dialog-service/dialog-service.service';
import { ImagesService } from '../../../services/images-service/images.service';


@Component({
  selector: 'app-update-product',
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.css'
})
export class UpdateProductComponent {
  
  isSpinning = false;

  productId: number;
  product: any;

  categories: any;
  selectedImageFile: File | null;
  imagePreview: string | null;
  updateProductForm: FormGroup;
  imageRemoved: boolean;


  constructor(
    private productService: ProductService,
    private activatedRoute: ActivatedRoute,
    private categoryService: CategoryService,
    private fb: FormBuilder,
    private dialogService: DialogService,
    private router: Router
  ){ 

    this.selectedImageFile = null; 
    // Imposto l'immagine di default
    this.imagePreview = null;
    this.imageRemoved = false;


    this.productId = this.activatedRoute.snapshot.params["id"]; 
    //console.log(this.productId)
    //qui bisogna mettere il nome della variabile presente nel file di routing, quindi "id"


    this.updateProductForm = this.fb.group({
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
    this.setProduct();
  } 

  
  setAllCategories(){
    this.categoryService.getAllCategories().subscribe((res) =>{
      //console.log(res);
      this.categories = res;
    })
  }


  setProduct(){
    this.productService.getProduct(this.productId).subscribe((res) => {
      console.log(res);
      this.product = res;

      // Popolo il form con i dati del prodotto
      this.updateProductForm.patchValue({
        name: this.product.name || '',
        description: this.product.description || '',
        price: this.product.price || '',
        categoryId: this.product.categoryId.toString() || '' //devo considerarlo come una stringa
      });

      //this.updateProductForm.get("categoryId")?.setValue(res.categoryId.toString());

      // Gestione dell'immagine
      if(this.product.image) {
        // L'immagine è in formato Base64
        this.imagePreview = 'data:image/jpeg;base64,' + this.product.image;
      }

      //console.log("Imagefile: " + this.selectedImageFile)
      //console.log("imagePreview: " + this.imagePreview)
    }, error => {
      console.error('Errore nel caricamento del prodotto:', error);
      this.dialogService.openErrorDialog("Errore", "Impossibile caricare il prodotto");
    });

  }



  removeImage(): void {
    this.imageRemoved = true;
    this.selectedImageFile = null;
    this.imagePreview = null;
  }


  onFileSelected(event: any){
    this.imageRemoved = false;
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
    this.imageRemoved = false;
    console.error('Errore nel caricamento dell\'immagine:', event);
    // Se l'immagine di default non viene trovata --> fallback
    this.selectedImageFile = null;
    if(this.product.image){
      // L'immagine è in formato Base64
      this.imagePreview = 'data:image/jpeg;base64,' + this.product.image;
      this.dialogService.openErrorDialog("Errore", "Errore nel caricamento dell'immagine! È stata reimpostata l'immagine originaria!");
    }
    else{
      this.imagePreview = null;
      this.dialogService.openErrorDialog("Errore", "Errore nel caricamento dell'immagine: immagine danneggiata! Seleziona un'altra immagine!");
    }
  }




  updateProduct(){
    if (this.updateProductForm.valid) {
      this.isSpinning = true;


      // Se non c'è né un file nuovo selezionato né un'immagine esistente invio image: null
      if ((!this.selectedImageFile && !this.product.image) || this.imageRemoved) {
        const productDTO = {
          id: this.productId,
          name: this.updateProductForm.get('name')?.value,
          description: this.updateProductForm.get('description')?.value,
          price: this.updateProductForm.get('price')?.value,
          image: null,
          categoryId: this.updateProductForm.get('categoryId')?.value
        };

        this.chiamataPut(productDTO);
      }
      // Se non c'è un file immagine selezionato ma c'è un'immagine esistente
      else if (!this.selectedImageFile && this.product.image) {
        ImagesService.convertBase64ToByteArray(this.product.image).then(byteArray => {
          const productDTO = {
            id: this.productId,
            name: this.updateProductForm.get('name')?.value,
            description: this.updateProductForm.get('description')?.value,
            price: this.updateProductForm.get('price')?.value,
            image: byteArray,
            categoryId: this.updateProductForm.get('categoryId')?.value
          };

          this.chiamataPut(productDTO);
        
        }).catch(error => {
          console.error('Errore nella conversione del file:', error);
          this.isSpinning = false;
          this.dialogService.openErrorDialog("Errore", "Seleziona un'altra immagine!");
        });
      }
      // Se c'è un nuovo file immagine selezionato lo converto ad array di byte
      else if(this.selectedImageFile) {
        ImagesService.convertFileToByteArray(this.selectedImageFile).then(byteArray => {
          const productDTO = {
            id: this.productId,
            name: this.updateProductForm.get('name')?.value,
            description: this.updateProductForm.get('description')?.value,
            price: this.updateProductForm.get('price')?.value,
            image: byteArray, // Invia come array di byte
            categoryId: this.updateProductForm.get('categoryId')?.value
          };

          this.chiamataPut(productDTO);

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

  chiamataPut(productDTO: any){
    console.log("DTO inviato al backend: \n" + productDTO.toString);

    this.productService.updateProduct(productDTO).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto modificato correttamente!");
        if(this.product.disabled == false){
          this.router.navigateByUrl("/admin/dashboard");
        }
        else{
          this.router.navigateByUrl("/admin/disable-products");
        }
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

