import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {

  constructor() { }

  static normalizeSpaces(str: string): string {
    if (!str) return '';

    // Rimuovo gli spazi iniziali
    let result = str.trimStart(); //oppure con regex: .replace(/^\s+/, ''); 
      //trimStart() rimuove tutti gli spazi all'inizio (prima della prima lettera)

    // Sostituisco gruppi di spazi (2 o più) con un solo spazio --> "   " diventa " "
    result = result.replace(/\s{2,}/g, ' ');

    return result;
}

}
