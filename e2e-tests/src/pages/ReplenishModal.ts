import { Page, Locator, expect } from '@playwright/test';

export class ReplenishModal {
  readonly page: Page;
  readonly modal: Locator;
  readonly qtyInput: Locator;
  readonly replenishBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.modal = page.locator('#stock-modal');
    this.qtyInput = page.locator('#replenish-qty');
    this.replenishBtn = page.locator('button:has-text("REPLENISH SALES FLOOR")');
  }

  async replenish(quantity: number) {
    await this.qtyInput.fill(quantity.toString());

    this.page.once('dialog', async dialog => {
      await dialog.accept();
    });

    await this.replenishBtn.click();
    await expect(this.modal).not.toHaveClass(/open/);
  }
}
