import { test, expect } from '@playwright/test';

test.describe('Store Manager Markdown Pricing', () => {

  test('Apply 20% seasonal markdown to garment and observe price update', async ({ page }) => {
    await page.goto('/');

    // Click MARKDOWN on first garment
    await page.locator('button:has-text("MARKDOWN")').first().click();
    await expect(page.locator('#markdown-modal')).toHaveClass(/open/);

    // Apply markdown
    await page.locator('button:has-text("APPLY MARKDOWN")').click();
    await expect(page.locator('#markdown-modal')).not.toHaveClass(/open/);

    // Verify strikethrough original price appears
    await expect(page.locator('.original-price').first()).toBeVisible();
  });
});
