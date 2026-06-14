import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { LocalStorageService } from './services/storage-service/local-storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'SoundHub Arcavacata - Strumenti musicali';

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private localStorageService: LocalStorageService
  ) {}

  ngOnInit(){
    if (!isPlatformBrowser(this.platformId)) return;

    let theme = this.localStorageService.getTheme() || 'classic';

    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = `assets/css/theme/${theme}.css`;
    document.head.append(link);
  }

}

