import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appNotLogo]',
  standalone: true
})
export class NotLogoDirective {

  constructor(private _elementImg: ElementRef) { }

  @HostListener('error')
  onError() :void {
    this._elementImg.nativeElement.src = "default-logo.png";
  }


}
