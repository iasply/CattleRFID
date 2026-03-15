import {type Locator, type Page} from '@playwright/test';

export class DashboardPage {
    readonly page: Page;
    readonly vetsCount: Locator;
    readonly cattleCount: Locator;
    readonly vaccinesCount: Locator;
    readonly newVetLink: Locator;
    readonly newCattleLink: Locator;

    constructor(page: Page) {
        this.page = page;
        this.vetsCount = page.getByTestId('dashboard-vets-count');
        this.cattleCount = page.getByTestId('dashboard-cattle-count');
        this.vaccinesCount = page.getByTestId('dashboard-vaccines-count');
        this.newVetLink = page.getByTestId('dashboard-new-vet-link');
        this.newCattleLink = page.getByTestId('dashboard-new-cattle-link');
    }

    async goto() {
        await this.page.goto('/admin/dashboard');
    }
}
