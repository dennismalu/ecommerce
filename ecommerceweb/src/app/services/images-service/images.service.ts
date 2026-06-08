import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  // Metodo per convertire File in Array di Byte
  public static convertFileToByteArray(file: File): Promise<number[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = () => {
        try {
          const arrayBuffer = reader.result as ArrayBuffer;
          // Converte ArrayBuffer in Uint8Array, poi in number[]
          const uint8Array = new Uint8Array(arrayBuffer);
          const byteArray = Array.from(uint8Array); // Converte in number[]
          
          console.log(`File convertito: ${byteArray.length} bytes`);
          resolve(byteArray);
        } catch (error) {
          reject(error);
        }
      };
      
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(file);
    });
  }


  // Metodo per convertire Base64 in Array di Byte
  public static convertBase64ToByteArray(base64String: string): Promise<number[]> {
    return new Promise((resolve, reject) => {
      try {
        // Rimuovi l'eventuale prefisso "data:image/..." se presente
        const base64Data = base64String.includes('base64,') 
          ? base64String.split('base64,')[1] 
          : base64String;
        
        // Decodifica Base64 in stringa binaria
        const binaryString = atob(base64Data);
        const byteArray = new Array(binaryString.length);
        
        for (let i = 0; i < binaryString.length; i++) {
          byteArray[i] = binaryString.charCodeAt(i);
        }
        
        console.log(`Base64 convertito: ${byteArray.length} bytes`);
        resolve(byteArray);
      } 
      catch (error) {
        reject(error);
      }
    });
  }


}
