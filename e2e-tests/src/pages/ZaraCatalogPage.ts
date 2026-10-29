import { Page, Locator, expect } from '@playwright/test';

export class ZaraCatalogPage {
  readonly page: Page;
  readonly rfidInput: Locator;
  readonly scanBtn: Locator;
  readonly garmentCards: Locator;
  readonly stockModal: Locator;
  readonly stockFloorQty: Locator;
  readonly stockBackQty: Locator;

  constructor(page: Page) {
    this.page = page;
    this.rfidInput = page.locator('#rfid-input');
    this.scanBtn = page.locator('button:has-text("SCAN SKU")');
    this.garmentCards = page.locator('.garment-card');
    this.stockModal = page.locator('#stock-modal');
    this.stockFloorQty = page.locator('#stock-floor-qty');
    this.stockBackQty = page.locator('#stock-back-qty');
  }

  async goto() {
    await this.page.goto('/');
    await expect(this.page).toHaveTitle(/ZARA STYLE/);
  }

  async scanBarcode(barcode: string) {
    await this.rfidInput.fill(barcode);
    await this.scanBtn.click();
    await expect(this.stockModal).toHaveClass(/open/);
  }

  async selectSizePill(garmentIndex: number, size: string) {
    const card = this.garmentCards.nth(garmentIndex);
    await card.locator(`.size-pill:has-text("${size}")`).click();
    await expect(this.stockModal).toHaveClass(/open/);
  }
}
