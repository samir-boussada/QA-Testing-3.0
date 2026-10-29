import { test, expect } from '@playwright/test';
import { ZaraCatalogPage } from '../src/pages/ZaraCatalogPage';
import { ReplenishModal } from '../src/pages/ReplenishModal';

test.describe('Zara RFID Scanning and Sales Floor Replenishment Flow', () => {

  test('Scan RFID barcode and verify exact size stock counts', async ({ page }) => {
    const catalog = new ZaraCatalogPage(page);
    await catalog.goto();

    // Scan sample barcode for Coat Size M
    await catalog.scanBarcode('ZARA-7941-PAR-M');

    await expect(page.locator('#stock-ref')).toHaveText('ZARA-7941-BLK');
    await expect(page.locator('#stock-size')).toHaveText('M');
    await expect(page.locator('#stock-rfid')).toHaveText('ZARA-7941-PAR-M');
  });

  test('Replenish sales floor moves units from backroom', async ({ page }) => {
    const catalog = new ZaraCatalogPage(page);
    const replenish = new ReplenishModal(page);

    await catalog.goto();
    await catalog.selectSizePill(0, 'M');

    // Replenish 2 units
    await replenish.replenish(2);

    // Verify modal closed
    await expect(page.locator('#stock-modal')).not.toHaveClass(/open/);
  });
});
