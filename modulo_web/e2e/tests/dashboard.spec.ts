import {expect, test} from '@playwright/test';
import {LoginPage} from '../src/pages/LoginPage';
import {DashboardPage} from '../src/pages/DashboardPage';

test.describe('Dashboard Flow', () => {
    let loginPage: LoginPage;
    let dashboardPage: DashboardPage;

    test.beforeEach(async ({page}) => {
        loginPage = new LoginPage(page);
        dashboardPage = new DashboardPage(page);

        // Ensure user is logged in
        await loginPage.goto();
        await loginPage.login('admin@cattlerfid.com', 'admin123');
        await expect(page).toHaveURL(/\/admin\/dashboard/);
    });

    test('should display summary stats correctly', async () => {
        await expect(dashboardPage.vetsCount).toBeVisible();
        await expect(dashboardPage.cattleCount).toBeVisible();
        await expect(dashboardPage.vaccinesCount).toBeVisible();
    });

    test('should navigate to new veterinarian form', async ({page}) => {
        await dashboardPage.newVetLink.click();
        await expect(page).toHaveURL(/\/admin\/veterinarians\/create/);
    });

    test('should navigate to new cattle form', async ({page}) => {
        await dashboardPage.newCattleLink.click();
        await expect(page).toHaveURL(/\/admin\/cattle\/create/);
    });
});
