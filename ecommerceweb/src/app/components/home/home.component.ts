import { isPlatformBrowser } from '@angular/common';
import { AfterViewInit, Component, ElementRef, Inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../services/storage-service/local-storage.service';
import { AuthService } from '../../services/auth-service/auth.service';
import { NzNotificationService } from 'ng-zorro-antd/notification';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {

  currentSlide = 0;
  totalSlides = 6;
  private autoSlideInterval: any;

  constructor(
    private elementRef: ElementRef,
    @Inject(PLATFORM_ID) private platformId: any,
    private router: Router,
    private localStorageService: LocalStorageService,
    private authService: AuthService,
    private NZnotification: NzNotificationService,
  ) {}


  
  ngOnInit() {
    // Verifica se siamo nel browser prima di eseguire codice che usa DOM APIs
    if (isPlatformBrowser(this.platformId)) {
      this.startAutoSlide();
      this.setupScrollAnimations();
    }
  }

  ngOnDestroy() {
    if (this.autoSlideInterval) {
      clearInterval(this.autoSlideInterval);
    }
  }

  // Funzione per passare alla slide successiva
  nextSlide(): void {
    this.pauseAutoSlide();
    this.currentSlide = (this.currentSlide + 1) % this.totalSlides;
    this.updateCarousel();
    this.resumeAutoSlide();
  }

  // Funzione per passare alla slide precedente
  prevSlide(): void {
    this.pauseAutoSlide();
    this.currentSlide = (this.currentSlide - 1 + this.totalSlides) % this.totalSlides;
    this.updateCarousel();
    this.resumeAutoSlide();
  }

  // Funzione per andare a una slide specifica
  goToSlide(index: number): void {
    this.currentSlide = index;
    this.updateCarousel();
  }

  // Aggiorna il carosello
  private updateCarousel(): void {
    // Verifica se siamo nel browser prima di accedere al DOM
    if (isPlatformBrowser(this.platformId)) {
      const carouselInner = document.querySelector('.carousel-inner') as HTMLElement;
      const indicators = document.querySelectorAll('.indicator');
      
      if (carouselInner) {
        carouselInner.style.transform = `translateX(-${this.currentSlide * 100}%)`;
      }

      // Aggiorna gli indicatori
      indicators.forEach((indicator, index) => {
        if (index === this.currentSlide) {
          indicator.classList.add('active');
        } else {
          indicator.classList.remove('active');
        }
      });
    }
  }

  // Auto-scroll delle slide
  private startAutoSlide(): void {
    this.autoSlideInterval = setInterval(() => {
      this.nextSlide();
    }, 5000); // Cambia slide ogni 5 secondi
  }

  // Ferma l'auto-scroll quando l'utente interagisce manualmente
  pauseAutoSlide(): void {
    if (this.autoSlideInterval) {
      clearInterval(this.autoSlideInterval);
    }
  }

  // Riprendi l'auto-scroll
  resumeAutoSlide(): void {
    this.startAutoSlide();
  }

  // Setup per le animazioni al scroll
  private setupScrollAnimations(): void {
    if (isPlatformBrowser(this.platformId) && 'IntersectionObserver' in window) {
      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('animated');
          }
        });
      }, {
        threshold: 0.1
      });

      // Osserva tutti gli elementi con la classe scroll-fade-in
      document.querySelectorAll('.scroll-fade-in').forEach(el => {
        observer.observe(el);
      });
    }
  }


}

