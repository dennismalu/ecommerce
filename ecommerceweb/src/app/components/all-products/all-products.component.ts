import { Component } from '@angular/core';
import { ProductService } from '../../services/api-services/product-service/product.service';
import { DialogService } from '../../services/dialog-service/dialog-service.service';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { AuthService } from '../../services/auth-service/auth.service';


//per le categorie e la scelta dell'ordinamento
interface SelectOption {
  value: string | number;
  label: string;
}


@Component({
  selector: 'app-all-products',
  templateUrl: './all-products.component.html',
  styleUrl: './all-products.component.css'
})
export class AllProductsComponent {
  isUserLoggedIn = false;
  isAdminLoggedIn = false;

  products: any[] = []; // Prodotti visibili nel template html
  originalProducts: any[] = []; // Conserva i prodotti originali
  categoryOptions: SelectOption[] = []; // Lista delle categorie disponibili

  // Opzioni per l'ordinamento
  sortOptions: SelectOption[] = [
    { value: 'name-asc', label: 'Nome (A-Z)' },
    { value: 'name-desc', label: 'Nome (Z-A)' },
    { value: 'price-asc', label: 'Prezzo (Crescente)' },
    { value: 'price-desc', label: 'Prezzo (Decrescente)' }
  ];


  selectedSort: string = 'name-asc';
  selectedCategory: number = -1; //-1 --> tutte le categorie (all)


  constructor(
    private productService: ProductService,
    private dialogService: DialogService,
    public localStorageService: LocalStorageService,
    private authService: AuthService
  ){ }


  ngOnInit(){
    this.setAllProducts();

    //OBIETTIVO: la pagina non deve venire renderizzata sul server (dove non 
    //esiste localStorage), ma solo sul client, dove i dati sono disponibili
    //this.isBrowser = isPlatformBrowser(this.platformId);
    //if (!this.isBrowser) return; // se sono sul server esco immediatamente dal metodo
    //IN REALTÀ il problema non si pone perché, sul server, non c'è nessun dato

    this.isUserLoggedIn = this.localStorageService.isUserLoggedIn();
    this.isAdminLoggedIn = this.localStorageService.isAdminLoggedIn();


    this.localStorageService.userLoggedInObservable.subscribe(status => {
      //console.log('User logged in status changed to:', status); //log
      this.isUserLoggedIn = status;
      //console.log('Current user status:', this.currentUserStatus); //log
    });

    this.localStorageService.adminLoggedInObservable.subscribe(status => {
      //console.log('Admin logged in status changed to:', status); //log
      this.isAdminLoggedIn = status;
      //console.log('Current user status:', this.currentUserStatus); //log
    });
  }


  setAllProducts(){
    this.products = []; //creo la lista vuota a cui aggiungere i singoli prodotti
    this.productService.getAllProducts().subscribe((res) =>{
      console.log(res);
      res.forEach((element: any) => {
        element.image = "data:image/jpeg; base64," + element.image;
        this.products.push(element);
      });
      this.originalProducts = [...this.products]; // Salva copia dei prodotti originali
      this.extractCategories(); // Estrai le categorie dai prodotti
      this.applyFilters(); // Applica i filtri iniziali
    })
  }



  // Estrae le categorie uniche dai prodotti
  extractCategories() {
    const categoryMap = new Map<string | number, string>();
    
    // Aggiungi opzione "Tutte le categorie"
    this.categoryOptions = [
      { value: -1, label: 'Tutte le categorie' } //-1 --> tutte le categorie (all)
    ];

    this.originalProducts.forEach(product => {
      if (product.categoryId && product.categoryName) {
        // Uso l'ID come value e il nome come label
        if (!categoryMap.has(product.categoryId)) {
          categoryMap.set(product.categoryId, product.categoryName);
        }
      }
    });

    // Aggiungi le categorie alle opzioni
    categoryMap.forEach((label, value) => {
      this.categoryOptions.push({ value, label });
    });

    // Ordina le categorie per nome
    this.categoryOptions.sort((a, b) => a.label.localeCompare(b.label));
  }



  // Applica sia filtro categoria che ordinamento
  applyFilters() {
    let filteredProducts: any[] = [];

    // Filtra per categoria
    // Se è stata selezionata una categoria mostro SOLO i prodotti di quella categoria
    if (this.selectedCategory !== -1) {
      this.originalProducts.forEach((product: any) => {
        if(product.categoryId === this.selectedCategory){
          filteredProducts.push(product);
        }
      });
    }
    // Se non è stata selezionata una categoria mostro TUTTI i prodotti
    else{
      this.originalProducts.forEach((product: any) => {
        filteredProducts.push(product);
      });
    }

    // Applica ordinamento
    this.sortProducts(filteredProducts);
  }



  // Ordina i prodotti in base alla selezione
  sortProducts(products: any[]) {
    const [field, order] = this.selectedSort.split('-');
    
    products.sort((a, b) => {
      let valueA = a[field];
      let valueB = b[field];

      // Se è il prezzo, converti in numero
      if (field === 'price') {
        valueA = Number(valueA);
        valueB = Number(valueB);
      } 
      // Per il nome, converti in minuscolo per ordinamento case-insensitive
      else {
        valueA = valueA.toLowerCase();
        valueB = valueB.toLowerCase();
      }

      if (valueA < valueB) {
        if(order === 'asc'){
          return -1;
        }
        return 1;
      }
      if (valueA > valueB) {
        if (order === 'asc') {
          return 1;
        } 
        return -1;
      }
      return 0;
    });

    this.products = products;
  }



  // Gestisce il cambio dell'ordinamento
  onSortChange() {
    this.applyFilters();
  }



  // Gestisce il cambio della categoria
  onCategoryChange() {
    this.applyFilters();
  }



  // Resetta tutti i filtri
  resetFilters() {
    this.selectedSort = 'name-asc';
    this.selectedCategory = -1;
    this.applyFilters();
  }



  // Resetta SOLO la categoria
  resetCategory() {
    this.selectedCategory = -1;
    this.applyFilters();
  }





  
  deleteProduct(id: number){
    console.log(id);
    this.productService.deleteProduct(id).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto eliminato correttamente!");
        //this.router.navigateByUrl("/admin/dashboard");
        this.setAllProducts(); //ricarico gli elementi
      },
      error: (error) => {
        console.log(error);
        if (error.status != 401) {
          this.dialogService.openErrorDialog("Errore", error.error);
        }
      }
    });
  }


}
