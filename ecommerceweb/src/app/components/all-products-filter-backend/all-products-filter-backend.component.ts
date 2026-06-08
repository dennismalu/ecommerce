import { Component } from '@angular/core';
import { ProductService } from '../../services/api-services/product-service/product.service';
import { DialogService } from '../../services/dialog-service/dialog-service.service';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { CategoryService } from '../../services/api-services/category-service/category.service';


//per le categorie e la scelta dell'ordinamento
interface SelectOption {
  value: number;
  label: string;
}


@Component({
  selector: 'app-all-products-filter-backend',
  templateUrl: './all-products-filter-backend.component.html',
  styleUrl: './all-products-filter-backend.component.css'
})
export class AllProductsFilterBackendComponent {
  isUserLoggedIn = false;
  isAdminLoggedIn = false;

  products: any[] = []; // Prodotti visibili nel template html
  categoryOptions: SelectOption[] = []; // Lista delle categorie disponibili

  // Opzioni per l'ordinamento
  sortOptions: SelectOption[] = [
    { value: 0, label: 'Nome (A-Z)' },
    { value: 1, label: 'Nome (Z-A)' },
    { value: 2, label: 'Prezzo (Crescente)' },
    { value: 3, label: 'Prezzo (Decrescente)' }
  ];


  selectedSort: number = 0;
  selectedCategory: number = -1; //-1 --> tutte le categorie (all)

  currentPage: number = 1;
  pageSize: number = 20; // default
  totalPages: number = 1;
  totalElements: number = 0;


  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private dialogService: DialogService,
    private localStorageService: LocalStorageService
  ){ }


  async ngOnInit(){
    await this.resetFilters(); //fa anche la GET dei prodotti - await: attende il completamento 
    //console.log(this.products)
    if(this.products.length != 0){
      this.setCategories(); //fa la GET delle categorie
    }

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



  // carica le categorie
  setCategories() {
    // Opzione "Tutte le categorie"
    this.categoryOptions.push({value: -1, label: "Tutte le categorie"});

    this.categoryService.getAllCategories().subscribe((res) =>{
      //console.log(res);
      res.forEach((category: any) => {
        this.categoryOptions.push({value: category.id, label: category.name});
      });

      // Ordino per nome (escludendo "Tutte le categorie" dall'ordinamento, resta in cima)
      const allOption = this.categoryOptions[0];
      const rest = this.categoryOptions.slice(1);
      rest.sort((a, b) => a.label.localeCompare(b.label));
      this.categoryOptions = [allOption, ...rest];
    })  
  }



  // fa la GET dei prodotti in base ai filtri definiti dall'utente
  setElements(): Promise<void> {
    this.products = []; //creo la lista vuota a cui aggiungere i singoli prodotti
    
    return new Promise((resolve, reject) => {
      // getAllProductsFilteredSortered(page, size, sort, category)
      this.productService.getAllProductsFilteredSortered(this.currentPage, this.pageSize, this.selectedSort, this.selectedCategory).subscribe({
        next: (res) =>{
        //console.log(res);
        this.totalPages = res.totalPages || 1; //1 in caso di valore mancante (null)
        this.totalElements = res.totalElements || 0; //0 in caso di valore mancante (null)

        res.list.forEach((element: any) => {
          element.image = "data:image/jpeg;base64," + element.image;
          this.products.push(element);
        });

        resolve();
      }});
    });
  }



  onFilterOrPageSizeChange() {
    this.currentPage = 1; // reset pagina quando cambio filtro
    this.setElements();
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.setElements();
    }
  }



  // Resetta tutti i filtri
  resetFilters() {
    this.selectedSort = 0; //ordinamento Nome (A-Z)
    this.selectedCategory = -1; //tutte le categorie (all)
    this.pageSize = 20;
    this.currentPage = 1;

    //restituisce la Promise
    return this.setElements();
  }



  // Resetta SOLO la categoria e il numero di pagina corrente
  resetCategory() {
    this.selectedCategory = -1; //tutte le categorie (all)
    this.currentPage = 1; // reset pagina quando cambio filtro
    this.setElements();
  }



  disableProduct(id: number){
    //console.log(id);
    this.productService.disableProduct(id).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto disattivato correttamente!");
        //this.router.navigateByUrl("/admin/dashboard");
        this.setElements(); //ricarico gli elementi
      },
      error: (error) => {
        console.log(error);
        if (error.status != 401) {
          this.dialogService.openErrorDialog("Errore", error.error);
        }
      }
    });
  }


  deleteProduct(id: number){
    //console.log(id);
    this.productService.deleteProduct(id).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto eliminato correttamente!");
        //this.router.navigateByUrl("/admin/dashboard");
        this.setElements(); //ricarico gli elementi
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
