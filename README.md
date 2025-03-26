# CustomMapProject
A graphical navigational map software, made for my computer science class. 

## General Description
### Who are the users of the software?
The users of the software is anyone interested in an application for tracking points of interest across a map. Contrary to most common and popular software, this is not necessarily based off of a real map. 
The map could be anything from a pirate's treasure map, a map of a fictional world, and ectara.  
### What is the purpose of the software?
The purpose of the software is to be able to add custom data to any image with the purpose of using it like, or akin to, a map. 
Users will be able to keep track of specific locations, information about those locations, information about how the locations relate to each other, predetermined paths, and much more. 
### Where and when will the software be used? 
The software is intended to be used mainly by those who need to be able to track map information on either a smaller and more unique scale. Having the ability to customize the map itself and data related to it is the main feature of the software.
### How does the software work? 
The software uses JavaFX to render a window in which a user can customize, navigate, save, and load maps.
See the UML diagram below for a more in-depth and technical view of how the software works. 
### Why would anyone want to us the software over existing processes? 
The main reason to use this software over existing ones is mainly the customization. Most popular mapping software are based upon real world maps, which in today's world translates into satellite imagery.
What this software offers is the ability to use your own custom maps, and then further work with them. The maps can be anything the user wishes, even if it doesn't necessarily follow the intended purpose of the software. 

## UML
![mpa1](https://github.com/user-attachments/assets/529b11ac-90a5-4dcb-8b19-29f408713e45)
Key: 
| Symbol    | Meaning |
| -------- | ------- |
| Hollow Red Square  | Private (fields)    |
| Solid Red Square  | Private (methods)|
| Solid Green Circle | Public     |
| Hollow Yellow Diamond    | Protected    |

UML diagram created using PlantUML

## Libraries
This software utilizes the JavaFX library.  

## TODO
- Add logic to buttons to prevent input errors
  - Includes prevent points with empty names 
- Implement panning and zooming
- Implement saves having custom map images
- Improve aesthetics of menu
- Add custom icons to points
- Make point's text more easily readable on various types of backgrounds
