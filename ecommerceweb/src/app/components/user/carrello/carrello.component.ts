import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../../services/storage-service/local-storage.service';
import { DialogService } from '../../../services/dialog-service/dialog-service.service';
import { CarrelloService } from '../../../services/api-services/carrello-service/carrello.service';

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

@Component({
  selector: 'app-carrello',
  templateUrl: './carrello.component.html',
  styleUrls: ['./carrello.component.css']
})
export class CarrelloComponent implements OnInit {
  cartItems: CartItem[] = [];
  enabledItems: CartItem[] = [];
  disabledItems: CartItem[] = [];

  isUserLoggedIn = false;

  constructor(
    private carrelloService: CarrelloService,
    private localStorageService: LocalStorageService,
    private router: Router,
    private dialogService: DialogService
  ) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    // ATTENZIONE: backend non ritorna paginato (come richiesto)
    // Supponiamo che carrelloService.getCarrello() ritorni Observable<CartItem[]>
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

        this.splitEnabledDisabled();
      },
      error: (err) => {
        console.error('Errore caricamento carrello', err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile caricare il carrello.');
      }
    });
  }

  private splitEnabledDisabled() {
    this.enabledItems = this.cartItems.filter(ci => !ci.product.disabled);
    this.disabledItems = this.cartItems.filter(ci => ci.product.disabled);
  }


  // funzioni per aumentare/diminuire quantità - valide SOLO per prodotti abilitati
  aumentaQuantita(item: CartItem) {
    if (item.product.disabled) return;
    item.quantity = item.quantity + 1;
    this.updateCartOnServer(item);
  }

  diminuisciQuantita(item: CartItem) {
    if (item.product.disabled) return;
    if (item.quantity > 1) {
      item.quantity = item.quantity - 1;
      this.updateCartOnServer(item);
    }
    else if(item.quantity == 1){
      this.rimuoviDalCarrello(item)
    }
  }

  private updateCartOnServer(item: CartItem) {
    // aggiorna quantità sul server (se il tuo backend lo richiede)
    //console.log(item.id, item.quantity) //debug
    this.carrelloService.updateQuantity(item.id, item.quantity).subscribe({
      next: () => {
        // opzionale: messaggio piccolo o nessuno
        this.loadCart();
      },
      error: (err) => {
        console.error('Errore update cart', err);
        this.dialogService.openErrorDialog('Errore', 'Impossibile aggiornare il carrello');
        this.loadCart();
      }
    });
  }

  // rimuovo dal carrello (richiamata dal template)
  rimuoviDalCarrello(item: CartItem) {
    // chiamo il servizio per rimuovere l'elemento
    this.carrelloService.removeFromCart(item.id).subscribe({
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

  //TODO
  ordinaCarrello(){

  }

  // calcola il totale considerando SOLO i prodotti abilitati
  getTotal(): number {
    // faccio calcolo numerico e arrotondo a 2 decimali
    let total = 0;
    for (const ci of this.enabledItems) {
      // calcolo righe, attenzione a tipi stringa/numero
      const price = Number(ci.product.price) || 0;
      const qty = Number(ci.quantity) || 0;
      total += price * qty;
    }
    // arrotondamento a 2 decimali
    return Math.round((total + Number.EPSILON) * 100) / 100;
  }

}