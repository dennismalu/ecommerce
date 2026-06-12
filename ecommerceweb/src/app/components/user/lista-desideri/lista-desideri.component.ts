import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../../services/storage-service/local-storage.service';
import { DialogService } from '../../../services/dialog-service/dialog-service.service';
import { CarrelloService } from '../../../services/api-services/carrello-service/carrello.service';
import { ListaDesideriService } from '../../../services/api-services/lista-desideri-service/lista-desideri.service';

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

interface ListaDesideriItem {
  id: number;
  product: Product;
}

@Component({
  selector: 'app-lista-desideri',
  templateUrl: './lista-desideri.component.html',
  styleUrl: './lista-desideri.component.css'
})
export class ListaDesideriComponent {
  cartItems: CartItem[] = [];
  listaDesideriItems: ListaDesideriItem[] = [];
  enabledItems: ListaDesideriItem[] = [];
  disabledItems: ListaDesideriItem[] = [];

  isUserLoggedIn = false;

  constructor(
    private carrelloService: CarrelloService,
    private listaDesideriService: ListaDesideriService,
    private localStorageService: LocalStorageService,
    private router: Router,
    private dialogService: DialogService
  ) {}

  ngOnInit() {
    this.loadCart();
    this.loadListaDesideri();
  }

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

  loadListaDesideri() {
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

        this.splitEnabledDisabled();
      },
      error: (err) => {
        console.error('Errore caricamento lista desideri', err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile caricare la lista desideri');
      }
    });
  }

  private splitEnabledDisabled() {
    this.enabledItems = this.listaDesideriItems.filter(ci => !ci.product.disabled);
    this.disabledItems = this.listaDesideriItems.filter(ci => ci.product.disabled);
  }


  // aggiungo al carrello (richiamata dal template)
  aggiungiAlCarrello(idProdotto: number) {
    // chiami il servizio per rimuovere l'elemento
    this.carrelloService.addToCart(idProdotto).subscribe({
      next: (res) => {
        this.loadCart();
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


  // rimuovo dal carrello (richiamata dal template)
  rimuoviDallaListaDesideri(item: ListaDesideriItem) {
    // chiamo il servizio per rimuovere l'elemento
    this.listaDesideriService.removeFromListaDesideri(item.id).subscribe({
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

  //TODO
  aggiungiTuttoAlCarrello(){

  }

  // calcola il totale considerando SOLO i prodotti abilitati
  getTotal(): number {
    // faccio calcolo numerico e arrotondo a 2 decimali
    let total = 0;
    for (const ci of this.enabledItems) {
      // calcolo righe, attenzione a tipi stringa/numero
      const price = Number(ci.product.price) || 0;
      total += price;
    }
    // arrotondamento a 2 decimali
    return Math.round((total + Number.EPSILON) * 100) / 100;
  }

}

