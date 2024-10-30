import { Component, OnInit, ViewChild } from '@angular/core';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import Swal from 'sweetalert2';
import { BaseChartDirective } from 'ng2-charts';
import * as ChartDataLabels from 'chartjs-plugin-datalabels';
import { ArcElement, BarController, BarElement, CategoryScale, Chart, ChartData, ChartOptions, ChartType, Legend, LinearScale, PieController, Title, Tooltip } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

@Component({
  selector: 'app-custom-reports',
  standalone: true,
  imports: [
    NavbarComponent,
    NgChartsModule
  ],
  templateUrl: './custom-reports.component.html',
  styleUrl: './custom-reports.component.scss'
})
export class CustomReportsComponent implements OnInit {
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;
  @ViewChild('chartResources', { static: false }) chartResources: BaseChartDirective | undefined;

  public barChartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {},
      y: {
        beginAtZero: true
      }
    }
  };
  

  
  public barChartLabels: string[] = [];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;

  public barChartData: ChartData<'bar'> = {
    labels: this.barChartLabels,
    datasets: [
      { 
        data: [], 
        label: 'Servicios populares', 
        backgroundColor: '#4B9CD3', 
        borderColor: '#4B9CD3',      
        borderWidth: 2               
      }
    ]
  };
  
  
  public pieChartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
  };

  public pieChartLabels: string[] = [];
  public pieChartType: ChartType = 'pie';
  public pieChartData: ChartData<'pie'> = { 
    labels: this.pieChartLabels,
    datasets: [
      {
        data: [],
        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
        hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56']
      }
    ]
  };


  public barChartLabelsResources: string[] = [];
  public barChartDataResources: ChartData<'bar'> = {
    labels: this.barChartLabelsResources,
    datasets: [
      { 
        data: [], 
        label: 'Recursos populares', 
        backgroundColor: '#4B9CD3', 
        borderColor: '#4B9CD3', 
        borderWidth: 2 
      }
    ]
  };

  public barChartOptionsResources: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {},
      y: {
        beginAtZero: true
      }
    }
  };
  


  constructor(private _userService: UserService) {
    Chart.register(BarController, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, PieController, ArcElement);
    Chart.register(ChartDataLabels);
  }



  ngOnInit(): void {
    this.loadReports();
  }

  loadReports() {
    this.loadPopularityServices();
    this.loadUsersPerRole();
    this.loadPopularityResources();
  }

  loadPopularityServices() {
    this._userService.getPopularityServices().subscribe((data) => {
      if(data) {
        this.updateChart(data);
      }else{
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se encontraron servicios populares'
        });
      }
    }, (error) => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.error.message
      });
    });
  }


  updateChart(data: any) {
  
    this.barChartLabels.length = 0;
    this.barChartLabels.push(...data.map((item: any) => item.name));
  
    this.barChartData.datasets[0].data.length = 0; 
    this.barChartData.datasets[0].data.push(...data.map((item: any) => item.total_requests));
    
    if (this.chart) {
      this.chart.update(); 
    }
  }

  loadUsersPerRole() {
    this._userService.getUserByRole().subscribe((data) => {
      if(data) {
        this.updatePieChart(data);
      }else{
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se encontraron usuarios por rol'
        });
      }
    }, (error) => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.error.message
      });
    });
  }

  updatePieChart(data: any) {
    this.pieChartLabels = data.map((item: any) => item.name);
    this.pieChartData.labels = this.pieChartLabels;
    this.pieChartData.datasets[0].data = data.map((item: any) => item.total_requests);

    if (this.chart?.chart) {
      this.chart.chart.update();
    }
  }

  loadPopularityResources() {
    this._userService.getPopularityResources().subscribe((data) => {
      if(data) {
        this.updateResourcesChart(data);
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se encontraron recursos populares'
        });
      }
    }, (error) => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: error.error.message
      });
    });
  }
  
  
  updateResourcesChart(data: any) {
    this.barChartLabelsResources.length = 0;
    this.barChartDataResources.datasets[0].data.length = 0;
  
    this.barChartLabelsResources.push(...data.map((item: any) => item.name));
    this.barChartDataResources.datasets[0].data.push(...data.map((item: any) => item.total_requests));
  
    if (this.chartResources && this.chartResources.chart) {
      this.chartResources.chart.update();
    } else {
    }
  }
  
  
  
  
  

}
