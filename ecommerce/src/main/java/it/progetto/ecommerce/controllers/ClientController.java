package it.progetto.ecommerce.controllers;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.services.carrello.CarrelloService;
import it.progetto.ecommerce.services.listaDesideri.ListaDesideriService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")   //gli endpoint possono essere richiamati solo con ruolo USER
@RequestMapping("/api/customer")
public class ClientController {

    private final CarrelloService carrelloService;
    private final ListaDesideriService listaDesideriService;

    @GetMapping("/carrello")
    public ResponseEntity<?> getCarrello() {
        List<CarrelloProductDTO> carrelloDTO = carrelloService.getCarrello();
        if(carrelloDTO == null || carrelloDTO.isEmpty()){
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(carrelloDTO, HttpStatus.OK); //stato 200
        }
    }

    @PostMapping("/carrello/add")
    public ResponseEntity<?> addToCarrello(
            @RequestParam long idProdotto
    ) {
        try {
            carrelloService.addToCarrello(idProdotto);
            return new ResponseEntity<>(HttpStatus.OK); //stato 200
        } catch (CustomException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PostMapping("/carrello/update")
    public ResponseEntity<?> updateCarrello(
        @RequestParam long idProdottoCarrello,
        @RequestParam int quantity
    ) {
        //System.out.println(idProdottoCarrello + "  ---  " + quantity); //debug
        try {
            carrelloService.updateCarrello(idProdottoCarrello, quantity);
            return new ResponseEntity<>(HttpStatus.OK); //stato 200
        } catch(CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PostMapping("/carrello/remove")
    public ResponseEntity<?> removeFromCarrello(
            @RequestParam long idProdottoCarrello
    ) {
        //System.out.println(idProdottoCarrello + "  ---  " + quantity); //debug
        try {
            carrelloService.removeFromCarrello(idProdottoCarrello);
            return new ResponseEntity<>(HttpStatus.OK); //stato 200
        } catch(CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }










    @GetMapping("/lista-desideri")
    public ResponseEntity<?> getListaDesideri() {
        List<ListaDesideriProductDTO> listaDesideriDTO = listaDesideriService.getListaDesideri();

        if(listaDesideriDTO == null || listaDesideriDTO.isEmpty()){
            return new ResponseEntity<>(new LinkedList<>(), HttpStatus.OK); //stato 200
        }
        else{
            return new ResponseEntity<>(listaDesideriDTO, HttpStatus.OK); //stato 200
        }
    }

    @PostMapping("/lista-desideri/add")
    public ResponseEntity<?> addToListaDesideri(
            @RequestParam long idProdotto
    ) {
        try {
            listaDesideriService.addToListaDesideri(idProdotto);
            return new ResponseEntity<>(HttpStatus.OK); //stato 200
        } catch (CustomException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PostMapping("/lista-desideri/remove")
    public ResponseEntity<?> removeFromListaDesideri(
            @RequestParam long idProdottoListaDesideri
    ) {
        try {
            listaDesideriService.removeFromListaDesideri(idProdottoListaDesideri);
            return new ResponseEntity<>(HttpStatus.OK); //stato 200
        } catch(CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

}


