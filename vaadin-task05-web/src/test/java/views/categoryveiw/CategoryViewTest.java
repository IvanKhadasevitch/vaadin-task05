package views.categoryveiw;

import com.vaadin.ui.Notification;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.AbstractUITest;

import java.util.*;

public class CategoryViewTest extends AbstractUITest {
    @Test
    public void testAddCategory() throws InterruptedException {
        driver.get(BASE_URL);
        Thread.sleep(1000); // Let the user actually see something!

        // go to category page
        WebElement categoryMenu = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//span[@class='v-menubar-menuitem v-menubar-menuitem-unchecked'][./span/@class='v-menubar-menuitem-caption'][contains(./span/text(), 'Category')]"
                )));
        categoryMenu.click();
        Thread.sleep(1000); // Let the user actually see something!

        WebElement addCategoryBtnCategoryView = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("addCategoryBtnCategoryView_id")));
        Assert.assertEquals("http://localhost:8080/category", driver.getCurrentUrl());
        Thread.sleep(1000); // Let the user actually see something!

        // determine Categories names for save
        Set<String> categoryNames = new LinkedHashSet<>(Arrays.asList("SeleniumCategory_1",
                "SeleniumCategory_2", "SeleniumCategory_3"));

        // show All filtered Categories
        getAllFilteredCategories("SeleniumCategory");
        Thread.sleep(3000); // Let the user actually see something!

        // delete if exist categories with name from categoryNames
        for (String categoryName : categoryNames) {
            deleteCategoryIfExist(categoryName);
        }
        int countBeforeSaveCategories = getAllFilteredCategories("SeleniumCategory").size();
        Thread.sleep(3000); // Let the user actually see something!

        // add categories from categoryNames
        for (String categoryName : categoryNames) {
            addCategory(categoryName, addCategoryBtnCategoryView);
        }
        int countAfterSaveCategories = getAllFilteredCategories("SeleniumCategory").size();

        Assert.assertEquals("Where saved not 3 categories with name contains [SeleniumCategory]",
                3, countAfterSaveCategories - countBeforeSaveCategories);
        Thread.sleep(3000); // Let the user actually see something!

    }

    private void addCategory(String categoryName, WebElement addCategoryBtnCategoryView)
            throws InterruptedException {
        addCategoryBtnCategoryView.click();
        WebElement nameTextFieldCategoryEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("nameTextFieldCategoryEditForm_id")));
        if (nameTextFieldCategoryEditForm.isEnabled()) {
            nameTextFieldCategoryEditForm.clear();
            nameTextFieldCategoryEditForm.sendKeys(categoryName);
        }
        Thread.sleep(1000); // Let the user actually see something!

        WebElement saveCategoryBtnCategoryEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("saveCategoryBtnCategoryEditForm_id")));

        if (saveCategoryBtnCategoryEditForm.isEnabled()) {
            saveCategoryBtnCategoryEditForm.click();
        }
        boolean isSaveBtnOn = true;
        do {
            // wait wile saving in DB
            try {
                if (saveCategoryBtnCategoryEditForm.isDisplayed()) {
                    System.out.println("Wait, please!!! Is Saving data in DB. Save Btn is -> ON!");
                }
            } catch (StaleElementReferenceException exp) {
                isSaveBtnOn = false;
            }
        } while (isSaveBtnOn);
        Thread.sleep(1000); // Let the user actually see something!
    }

    private void deleteCategoryIfExist(String categoryName) throws InterruptedException {
        // find Category if exist
        List<WebElement> finedCategories = getAllFilteredCategories(categoryName);
        for (WebElement finedCategory : finedCategories) {
            // take Grid Cells
            List<WebElement> gridCells = finedCategory.findElements(By.xpath(".//td[contains(@class, v-grid-cell)]"));
            // take 2-end cell - CategoryName
            WebElement categoryNameCell = gridCells.get(1);
            String categoryNameFromGrid = null;
            if (categoryNameCell.isEnabled()) {
                categoryNameFromGrid = categoryNameCell.getText();
            }
            if (categoryName.equalsIgnoreCase(categoryNameFromGrid)) {
                // take 1-rst cell - CategoryName checkbox
                WebElement checkBoxCell = gridCells.get(0);
                WebElement checkBox = checkBoxCell.findElement(By.xpath(".//span[contains(@class, v-grid-selection-checkbox)]"));
                // select Category with name=categoryName
                checkBox.click();
            }
        }
        Thread.sleep(1000); // Let the user actually see something!

        // delete selected Category
        WebElement deleteCategoryBtn = driver.findElement(By.id("deleteCategoryBtnCategoryView_id"));
        if (deleteCategoryBtn.isEnabled()) {
            deleteCategoryBtn.click();
        }
        Thread.sleep(1000); // Let the user actually see something!
    }

    private List<WebElement> getAllFilteredCategories(String filterByName) throws InterruptedException {
        // find All Categories where name contains filterByName
        WebElement filterByNameTextFieldCategoryView = driver.findElement(By.id("filterByNameTextFieldCategoryView_id"));
        if (filterByNameTextFieldCategoryView.isEnabled()) {
            filterByNameTextFieldCategoryView.clear();
            filterByNameTextFieldCategoryView.sendKeys(filterByName);
        }
        Thread.sleep(1000); // Let the user actually see something!

        return driver.findElements(By.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
    }
}
