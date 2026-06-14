import { Component } from '@angular/core';
import { ProductService } from '../../services/api-services/product-service/product.service';
import { DialogService } from '../../services/dialog-service/dialog-service.service';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { CategoryService } from '../../services/api-services/category-service/category.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UtilsService } from '../../services/utils-service/utils.service';
import { CarrelloService } from '../../services/api-services/carrello-service/carrello.service';
import { ListaDesideriService } from '../../services/api-services/lista-desideri-service/lista-desideri.service';
import { ActivatedRoute, Router } from '@angular/router';


//per le categorie e la scelta dell'ordinamento
interface SelectOption {
  value: number;
  label: string;
}

//per il carrello
interface Product {
  id: number;
  name: string;
  image?: string; // base64 or url
  description?: string | null;
  price: number;
  categoryId?: number;
  categoryName?: string;
  disabled?: boolean; // true => non abilitato
  // altri campi se servono
}

interface CartItem {
  id: number;
  product: Product;
  quantity: number;
}

//per la lista desideri
interface ListaDesideriItem {
  id: number;
  product: Product;
}


@Component({
  selector: 'app-search-product',
  templateUrl: './search-product.component.html',
  styleUrl: './search-product.component.css'
})
export class SearchProductComponent{
  isUserLoggedIn = false;
  isAdminLoggedIn = false;

  searchDone: boolean;
  searchForm: FormGroup;
  nameSearch: string;
  withDisabled: boolean; //SOLO ADMIN

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

  cartItems: CartItem[] = [];
  listaDesideriItems: ListaDesideriItem[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private carrelloService: CarrelloService,
    private listaDesideriService: ListaDesideriService,
    private productService: ProductService,
    private categoryService: CategoryService,
    private dialogService: DialogService,
    private localStorageService: LocalStorageService,
    private fb: FormBuilder
  ){ 
    this.nameSearch = "";
    this.searchDone = false;
    this.withDisabled = false; //SOLO ADMIN
    
    this.searchForm = this.fb.group({
      name: ["", [Validators.required]],
      includeDisabled: [false] 
    });
  }


  async ngOnInit(){
    this.selectedSort = Number(this.route.snapshot.queryParamMap.get('sort')) || 0;
    this.selectedCategory = Number(this.route.snapshot.queryParamMap.get('category')) || -1; //-1 --> tutte le categorie (all)
  
    this.currentPage = Number(this.route.snapshot.queryParamMap.get('page')) || 1;
    this.pageSize = Number(this.route.snapshot.queryParamMap.get('size')) || 20; // default
    this.nameSearch = this.route.snapshot.queryParamMap.get('search') || ""; // default - NOOOOO cast a String --> ERRORISSIMO!


    if(this.nameSearch != null && this.nameSearch != undefined && this.nameSearch != ""){
      this.searchDone = true;
    }
    

    this.setCategories(); //fa la GET delle categorie

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

    if(this.isUserLoggedIn){
      this.loadCart();
      this.loadListaDesideri();
    }

    // Ricarica gli elementi ai cambi di query param nella stessa route
    this.route.queryParams.subscribe(params => {
      const newSort = Number(params['sort']) || 0;
      const newCategory = Number(params['category']) || -1;
      const newPage = Number(params['page']) || 1;
      const newSize = Number(params['size']) || 20;
      const newSearch = params['search'] || ''; //NOOOOO cast a String --> ERRORISSIMO!

      //se i parametri cambiano (frecce indietro e avanti nel browser) aggiorno i campi e ricarico i prodotti
      if ( 
          (
          newSort !== this.selectedSort ||
          newCategory !== this.selectedCategory ||
          newPage !== this.currentPage ||
          newSize !== this.pageSize ||
          newSearch !== this.nameSearch
        ) && (newSearch != null && newSearch != undefined && newSearch != "")
      ) {
        this.selectedSort = newSort;
        this.selectedCategory = newCategory;
        this.currentPage = newPage;
        this.pageSize = newSize;
        this.nameSearch = newSearch;
      }

      //se sto solo interagendo con la pagina i valori vengono settati dai metodi prima del cambio dei query param
      this.setElements();    
      this.searchForm.patchValue({
        name: this.nameSearch
      });
      
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
    });
  }



  //viene eseguito SOLO quando si preme sul tasto "Cerca" nella view
  searchProduct(){
    if(this.searchForm.valid){
      this.searchDone = true;

      let nameSearchForm: string = this.searchForm.value.name;
      this.nameSearch = UtilsService.normalizeSpaces(nameSearchForm);
      this.searchForm.patchValue({
        name: this.nameSearch
      });

      this.onNameOrFilterOrPageSizeChange(); //ripristina il numero di pagina e chiama setElements()
    }
    else{
      this.dialogService.openErrorDialog("Errore", "Inserisci il nome del prodotto da cercare!");
    }
  }



  // fa la GET dei prodotti in base ai filtri definiti dall'utente
  setElements(){
    if(this.searchDone){
      this.products = []; //creo la lista vuota a cui aggiungere i singoli prodotti

      //user e utente non registrato
      if(!this.isAdminLoggedIn){
        this.productService.searchProductByTitleFilteredSortered(this.nameSearch, this.currentPage, this.pageSize, this.selectedSort, this.selectedCategory).subscribe((res) =>{
          //console.log(res);
          this.totalPages = res.totalPages || 1; //1 in caso di valore mancante (null)
          this.totalElements = res.totalElements || 0; //0 in caso di valore mancante (null)

          res.list.forEach((element: any) => {
            element.image = "data:image/jpeg;base64," + element.image;
            this.products.push(element);
          });
        });
      }
      else{ // if(this.isAdminLoggedIn)
        this.withDisabled = this.searchForm.value.includeDisabled;
        this.productService.searchProductByTitleWithDisabledFilteredSortered(this.nameSearch, this.withDisabled, this.currentPage, this.pageSize, this.selectedSort, this.selectedCategory).subscribe((res) =>{
          //console.log(res);
          this.totalPages = res.totalPages || 1; //1 in caso di valore mancante (null)
          this.totalElements = res.totalElements || 0; //0 in caso di valore mancante (null)

          res.list.forEach((element: any) => {
            element.image = "data:image/jpeg;base64," + element.image;
            this.products.push(element);
          });
        });
      }
    }
  }



  onNameOrFilterOrPageSizeChange() {
    this.currentPage = 1; // reset pagina quando cambio filtro
    this.router.navigate(['/search-product'], { queryParams: { page: this.currentPage, size: this.pageSize, sort: this.selectedSort, category: this.selectedCategory, search: this.nameSearch } });
    //this.setElements();
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.router.navigate(['/search-product'], { queryParams: { page: this.currentPage, size: this.pageSize, sort: this.selectedSort, category: this.selectedCategory, search: this.nameSearch } });
      //this.setElements();
    }
  }



  // Resetta tutti i filtri
  resetFilters() {
    this.selectedSort = 0; //ordinamento Nome (A-Z)
    this.selectedCategory = -1; //tutte le categorie (all)
    this.pageSize = 20;
    this.currentPage = 1;

    this.router.navigate(['/search-product'], { queryParams: { page: this.currentPage, size: this.pageSize, sort: this.selectedSort, category: this.selectedCategory, search: this.nameSearch } });
    //this.setElements();
  }



  // Resetta SOLO la categoria e il numero di pagina corrente
  resetCategory() {
    this.selectedCategory = -1; //tutte le categorie (all)
    this.currentPage = 1; // reset pagina quando cambio filtro
    this.router.navigate(['/search-product'], { queryParams: { page: this.currentPage, size: this.pageSize, sort: this.selectedSort, category: this.selectedCategory, search: this.nameSearch } });
    //this.setElements();
  }










  //ADMIN

  disableProduct(id: number){
    //console.log(id);
    this.productService.disableProduct(id).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto disabilitato correttamente!");
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

    enableProduct(id: number){
    //console.log(id);
    this.productService.enableProduct(id).subscribe({
      next: (response) => {
        //console.log(response);
        this.dialogService.openSuccessDialog("Operazione completata con successo!", "Prodotto abilitato correttamente!");
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














  //USER

  loadCart() {
    this.carrelloService.getCarrello().subscribe({
      next: (items: any[]) => {
        // mappatura difensiva
        this.cartItems = items.map(it => ({
          product: {
            id: it.product.id,
            name: it.product.name,
            image: it.product.image ? ('data:image/jpeg;base64,' + it.product.image) : it.product.image,
            description: it.product.description,
            price: it.product.price,
            categoryId: it.product.categoryId,
            categoryName: it.product.categoryName,
            disabled: it.product.disabled
          },
          id: it.id,
          quantity: it.quantity
        }));
      },
      error: (err) => {
        console.error('Errore caricamento carrello', err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile caricare il carrello.');
      }
    });
  }


  // aggiungo al carrello (richiamata dal template)
  aggiungiAlCarrello(idProdotto: number) {
    // chiami il servizio per rimuovere l'elemento
    this.carrelloService.addToCart(idProdotto).subscribe({
      next: (res) => {
        this.loadCart();
        // aggiorno la vista localmente
        //this.cartItems = this.cartItems.filter(ci => ci.product.id !== item.product.id);
        //this.splitEnabledDisabled();
        //this.dialogService.openSuccessDialog('Rimosso', 'Prodotto rimosso dal carrello');
      },
      error: (err) => {
        console.error(err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile aggiungere il prodotto dal carrello');
        this.loadCart();
      }
    });
  }


  // rimuovo dal carrello (richiamata dal template)
  rimuoviDalCarrello(idProdotto: number) {
    let cartItem: CartItem|null = null;
    for(const item of this.cartItems){
      if(item.product.id == idProdotto){
        cartItem = item;
        break;
      }
    }
    
    if(cartItem != null){
      // chiamo il servizio per rimuovere l'elemento
      this.carrelloService.removeFromCart(cartItem.id).subscribe({
        next: (res) => {
          this.loadCart();
          // aggiorno la vista localmente
          //this.cartItems = this.cartItems.filter(ci => ci.product.id !== item.product.id);
          //this.splitEnabledDisabled();
          //this.dialogService.openSuccessDialog('Rimosso', 'Prodotto rimosso dal carrello');
        },
        error: (err) => {
          console.error(err);
          this.dialogService.openErrorDialog('Errore', 'Impossibile rimuovere il prodotto dal carrello');
          this.loadCart();
        }
      });
    }
  }

  //true se il prodotto è nella carta
  isInCart(productId: number): boolean {
    for(const item of this.cartItems){
      if(item.product.id == productId){
        return true;
      }
    }

    return false;
  }


  
  loadListaDesideri(){
    this.listaDesideriService.getListaDesideri().subscribe({
      next: (items: any[]) => {
        // mappatura difensiva
        this.listaDesideriItems = items.map(it => ({
          product: {
            id: it.product.id,
            name: it.product.name,
            image: it.product.image ? ('data:image/jpeg;base64,' + it.product.image) : it.product.image,
            description: it.product.description,
            price: it.product.price,
            categoryId: it.product.categoryId,
            categoryName: it.product.categoryName,
            disabled: it.product.disabled
          },
          id: it.id
        }));
      },
      error: (err) => {
        console.error('Errore caricamento lista desideri', err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile caricare la lista desideri');
      }
    });
  }

  // aggiungo alla lista desideri (richiamata dal template)
  aggiungiAllaListaDesideri(idProdotto: number) {
    // chiami il servizio per rimuovere l'elemento
    this.listaDesideriService.addToListaDesideri(idProdotto).subscribe({
      next: (res) => {
        this.loadListaDesideri();
      },
      error: (err) => {
        console.error(err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile aggiungere il prodotto alla lista desideri');
        this.loadListaDesideri();
      }
    });
  }


  // rimuovo dal carrello (richiamata dal template)
  rimuoviDallaListaDesideri(idProdotto: number) {
    let listaDesideriItem: ListaDesideriItem|null = null;
    for(const item of this.listaDesideriItems){
      if(item.product.id == idProdotto){
        listaDesideriItem = item;
        break;
      }
    }
    
    if(listaDesideriItem != null){
      // chiamo il servizio per rimuovere l'elemento
      this.listaDesideriService.removeFromListaDesideri(listaDesideriItem.id).subscribe({
        next: (res) => {
          this.loadListaDesideri();
        },
        error: (err) => {
          console.error(err);
          this.dialogService.openErrorDialog('Errore', 'Impossibile rimuovere il prodotto dalla lista desideri');
          this.loadListaDesideri();
        }
      });
    }
  }

  //true se il prodotto è nella lista desideri
  isInListaDesideri(productId: number): boolean {
    for(const item of this.listaDesideriItems){
      if(item.product.id == productId){
        return true;
      }
    }

    return false;
  }






















  erroreUtenteNonLoggato(){
    this.dialogService.openErrorDialog('Errore', 'Accedi al sito per aggiungere il prodotto al carrello');
  }

}

